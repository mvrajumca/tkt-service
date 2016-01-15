
package com.tkt.ticketservicing.core;

import java.util.List;

/**
 * @author Raju
 *
 */
public class Level {

	private int levelId;

	private int levelPrice;

	private List<Seat> seats;

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	public int getLevelPrice() {
		return levelPrice;
	}

	public void setLevelPrice(int levelPrice) {
		this.levelPrice = levelPrice;
	}

}
