package com.tkt.ticketservicing.infra;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.tkt.ticketservicing.core.Seat;

/**
 * @author Raju
 *
 */
public class TicketReleaseExecutor implements Callable<Object> {

	private final static Logger logger = Logger.getLogger(TicketReleaseExecutor.class);

	private List<Seat> holdSeats;

	private Map<String, List<Seat>> onHoldSeats;

	private String holdId;

	public TicketReleaseExecutor(List<Seat> holdSeats, Map<String, List<Seat>> onHoldSeats, String holdId) {
		this.holdSeats = holdSeats;
		this.onHoldSeats = onHoldSeats;
		this.holdId = holdId;
	}

	public Object call() throws Exception {
		boolean canRemoveHold = false;
		for (Seat seat : holdSeats) {
			logger.debug("Releasing seats..." + seat.getSeatId());
			if (!seat.isReserved()) {
				seat.setHeld(false);
				canRemoveHold = true;
			}
		}
		if (canRemoveHold) {
			logger.debug("Cleaned up hold reference..."+holdId);
			onHoldSeats.remove(holdId);
		}
		return holdSeats;
	}

}
