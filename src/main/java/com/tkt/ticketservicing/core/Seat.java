
package com.tkt.ticketservicing.core;

/**
 * @author Raju
 *
 */
public class Seat {

	private int seatId;

	private boolean isReserved;

	private boolean isHeld;

	public int getSeatId() {
		return seatId;
	}

	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}

	public boolean isReserved() {
		return isReserved;
	}

	public void setReserved(boolean isReserved) {
		this.isReserved = isReserved;
	}

	public boolean isHeld() {
		return isHeld;
	}

	public void setHeld(boolean isHeld) {
		this.isHeld = isHeld;
	}

	@Override
	public int hashCode() {
		return seatId;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj == null) {
			isEqual = false;
		} else {
			Seat seat = (Seat) obj;
			if (seat.seatId == seatId) {
				isEqual = true;
			}
		}
		return isEqual;
	}

}