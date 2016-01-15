package com.tkt.ticketservicing.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.tkt.ticketservicing.core.Level;
import com.tkt.ticketservicing.core.Seat;
import com.tkt.ticketservicing.core.SeatHold;
import com.tkt.ticketservicing.exception.TicketSystemException;

/**
 * This class acts as a repository and performs operations on data access layer.
 * Can be modified to extend the solution for distributed JVMs.
 * 
 * @author Raju
 */
public class TicketRepository {

	private Map<Integer, Level> levels = SeatsInfra.getLevels();

	private Map<String, List<Seat>> onHoldSeats = new HashMap<String, List<Seat>>();

	private final ReentrantLock lock = new ReentrantLock();

	private final static Logger logger = Logger.getLogger(TicketRepository.class);

	private static TicketRepository repository = null;

	private ScheduledExecutorService scheduledExecutorService;

	private static final int HOLD_EXECUTOR_TIME_IN_SECONDS = 2;

	private TicketRepository() {
		scheduledExecutorService = Executors.newScheduledThreadPool(4);
		logger.debug("Initialized ExecutorService...");
	}

	public static TicketRepository initialize() {
		if (repository == null) {
			synchronized (TicketRepository.class) {
				if (repository == null) {
					repository = new TicketRepository();
				}
			}
		}
		return repository;
	}

	/**
	 * Gets number of seats based on availability and based on provided venue
	 * level.
	 * 
	 * @param venueLevel
	 * @return
	 * @throws TicketSystemException
	 */
	public int getNumberOfSeatsAvailable(Optional<Integer> venueLevel) throws TicketSystemException {
		int nbrOfSeats = 0;
		Integer levelId = venueLevel != null ? venueLevel.isPresent() ? venueLevel.get() : 0 : 0;
		if (levelId >= 0 && levelId <= levels.size()) {
			if (levelId == 0) {
				for (Entry<Integer, Level> lvls : levels.entrySet()) {
					nbrOfSeats = totalAvailableSeatsBasedOnLevel(lvls.getValue());
				}
			} else {
				Level level = levels.get(levelId);
				nbrOfSeats = totalAvailableSeatsBasedOnLevel(level);
			}
		} else {
			logger.debug("Invalid level info...");
			throw new TicketSystemException("Invalid Level info.");
		}
		return nbrOfSeats;
	}

	/**
	 * @param nbrOfSeats
	 * @param lvls
	 * @return
	 */
	private int totalAvailableSeatsBasedOnLevel(Level level) {
		int nbrOfSeats = 0;
		for (Seat seat : level.getSeats()) {
			if (seat.isHeld() || seat.isReserved()) {
				continue;
			} else {
				nbrOfSeats++;
			}
		}
		return nbrOfSeats;
	}

	/**
	 * This method iterates from provided minimum level to the maximum level. It
	 * finds and holds the number of seats requested. Sets the hold time as 2
	 * seconds.
	 * 
	 * @param numSeats
	 * @param minLevel
	 * @param maxLevel
	 * @param customerEmail
	 * @return
	 * @throws TicketSystemException
	 * @throws InterruptedException
	 */
	public SeatHold findAndholdSeats(int numSeats, int minLevel, int maxLevel, String customerEmail)
			throws TicketSystemException, InterruptedException {
		if (minLevel == 0 || maxLevel == 0 || minLevel > maxLevel || maxLevel > SeatsInfra.values().length) {
			throw new TicketSystemException("Invalid Levels info provided.");
		}
		SeatHold seatHold = null;
		do {
			Level level = levels.get(minLevel);
			try {
				acquireLock();
				int nbrOfSeatsBasedOnLevel = totalAvailableSeatsBasedOnLevel(level);
				if (nbrOfSeatsBasedOnLevel >= numSeats) {
					logger.debug("Proceeding to hold seats. Level " + level.getLevelId() + ", nbrOfSeatsBasedOnLevel "
							+ nbrOfSeatsBasedOnLevel + ", Requested Number of Seats " + numSeats);
					seatHold = holdSeats(level, numSeats, customerEmail);
				} else {
					minLevel++;
				}
			} finally {
				lock.unlock();
			}
		} while (seatHold == null && minLevel <= maxLevel);
		if (minLevel > maxLevel && seatHold == null) {
			throw new TicketSystemException("Number of seats requested are not available in any of the levels.");
		}
		return seatHold;
	}

	/**
	 * Acquire lock to handle thread safe hold seats. Max thread wait duration
	 * is 2 secs.
	 * 
	 * @throws InterruptedException
	 * @throws TicketSystemException
	 */
	private void acquireLock() throws InterruptedException, TicketSystemException {
		boolean isLockAquired = lock.tryLock(2L, TimeUnit.SECONDS);
		logger.debug("Thread " + Thread.currentThread().getId() + " acquired lock at " + System.currentTimeMillis());
		if (!isLockAquired) {
			logger.debug("Thread " + Thread.currentThread().getId() + " execution time exceeded 2 secs.");
			throw new TicketSystemException("Unable to aquire lock in 2 secs");
		}
	}

	/**
	 * @param level
	 */
	private SeatHold holdSeats(Level level, int nbrOfSeatsToHold, String customerEmail) {
		List<Seat> onHoldSeats = new ArrayList<Seat>();
		SeatHold seatHold = null;
		int totalSeatsHeld = 1;
		int[] seats = new int[nbrOfSeatsToHold];
		for (Seat seat : level.getSeats()) {
			if (totalSeatsHeld <= nbrOfSeatsToHold && !seat.isHeld() && !seat.isReserved()) {
				seat.setHeld(true);
				seats[totalSeatsHeld - 1] = seat.getSeatId();
				onHoldSeats.add(seat);
				totalSeatsHeld++;
			}
			if (totalSeatsHeld > nbrOfSeatsToHold) {
				seatHold = makeSeatHold(level, onHoldSeats, customerEmail, seats);
				break;
			}
		}
		return seatHold;
	}

	private SeatHold makeSeatHold(Level level, List<Seat> holdSeats, String customerEmail, int[] seats) {
		String holdId = UUID.randomUUID().toString();
		onHoldSeats.put(holdId, holdSeats);
		logger.debug("Seats holded with the hold reference number " + holdId);
		initiateExecutorsForHoldCheck(holdSeats, holdId);
		SeatHold seatHold = new SeatHold();
		seatHold.setNbrOfSeats(holdSeats.size());
		seatHold.setHoldId(holdId);
		seatHold.setTotalAmount(level.getLevelPrice() * holdSeats.size());
		seatHold.setLevelId(level.getLevelId());
		seatHold.setCustomerEmail(customerEmail);
		seatHold.setHoldSeats(seats);
		return seatHold;
	}

	private void initiateExecutorsForHoldCheck(List<Seat> holdSeats, String holdId) {
		TicketReleaseExecutor releaseExecutor = new TicketReleaseExecutor(holdSeats, onHoldSeats, holdId);
		scheduledExecutorService.schedule(releaseExecutor, HOLD_EXECUTOR_TIME_IN_SECONDS, TimeUnit.SECONDS);
	}

	/**
	 * Creates reservation using the hold reference.
	 * 
	 * @param holdId
	 * @param customerEmail
	 * @return
	 * @throws TicketSystemException
	 */
	public String reserveSeatsOnHold(String holdId, String customerEmail) throws TicketSystemException {
		List<Seat> holdSeats = onHoldSeats.get(holdId);
		if (holdSeats == null) {
			logger.debug("Invalid hold info " + holdId);
			throw new TicketSystemException("Invalid hold information.");
		}
		int nbrOfSeatsReserved = 0;
		for (Seat seat : holdSeats) {
			if (seat.isHeld()) {
				seat.setReserved(true);
				nbrOfSeatsReserved++;
			}
		}
		if (nbrOfSeatsReserved != holdSeats.size()) {
			onHoldSeats.remove(holdId);
			throw new TicketSystemException("Unable to confirm the reservation.");
		}
		return holdId;
	}
}