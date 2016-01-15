package com.tkt.ticketservicing.service;

import java.util.Optional;

import com.tkt.ticketservicing.core.SeatHold;
import com.tkt.ticketservicing.exception.TicketSystemException;
import com.tkt.ticketservicing.infra.TicketRepository;

/**
 * This class can be exposed as a REST resource.
 * 
 * @author Raju
 *
 */
public class TicketServiceResource implements TicketService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.walmart.ticketservicing.service.TicketService#numSeatsAvailable(java.
	 * util.Optional)
	 */
	private final TicketRepository repository = TicketRepository.initialize();

	public int numSeatsAvailable(Optional<Integer> venueLevel) throws TicketSystemException {
		return repository.getNumberOfSeatsAvailable(venueLevel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.walmart.ticketservicing.service.TicketService#findAndHoldSeats(int,
	 * java.util.Optional, java.util.Optional, java.lang.String)
	 */
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) throws TicketSystemException, InterruptedException {
		return repository.findAndholdSeats(numSeats, minLevel != null ? minLevel.isPresent() ? minLevel.get() : 0 : 0,
				maxLevel != null ? maxLevel.isPresent() ? maxLevel.get() : 0 : 0, customerEmail);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walmart.ticketservicing.service.TicketService#reserveSeats(int,
	 * java.lang.String)
	 */
	public String reserveSeats(String seatHoldId, String customerEmail) throws TicketSystemException {
		return repository.reserveSeatsOnHold(seatHoldId, customerEmail);
	}
}