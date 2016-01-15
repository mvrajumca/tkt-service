
package com.tkt.ticketservicing.core;

/**
 * @author Raju
 *
 */
public class SeatHold {

	private int nbrOfSeats;

	private String holdId;

	private int levelId;

	private int[] holdSeats;

	private int totalAmount;

	private String customerEmail;

	public int[] getHoldSeats() {
		return holdSeats;
	}

	public void setHoldSeats(int[] holdSeats) {
		this.holdSeats = holdSeats;
	}

	public int getNbrOfSeats() {
		return nbrOfSeats;
	}

	public void setNbrOfSeats(int nbrOfSeats) {
		this.nbrOfSeats = nbrOfSeats;
	}

	public String getHoldId() {
		return holdId;
	}

	public void setHoldId(String holdId) {
		this.holdId = holdId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

}
