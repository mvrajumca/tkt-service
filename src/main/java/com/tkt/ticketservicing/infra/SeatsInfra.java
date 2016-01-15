
package com.tkt.ticketservicing.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tkt.ticketservicing.core.Level;
import com.tkt.ticketservicing.core.Seat;

/**
 * This class acts as a infra layer to hold the seats and level information.
 * 
 * @author Raju
 */
public enum SeatsInfra {

	ORCHESTRA(1, "Orchestra", 100, 25, 50), MAIN(2, "Main", 75, 20, 100), BALCONY1(3, "Balcony1", 50, 15,
			100), BALCONY2(4, "Balcony2", 40, 15, 100);

	private final int levelId;

	private final String levelName;

	private final int seatPrice;

	private final int nbrOfRows;

	private final int nbrOfSeatsInRow;

	private static final Map<Integer, Level> levels = initializeLevels();

	private SeatsInfra(int levelId, String levelName, int seatPrice, int nbrOfRows, int nbrOfSeatsInRow) {
		this.levelId = levelId;
		this.levelName = levelName;
		this.seatPrice = seatPrice;
		this.nbrOfRows = nbrOfRows;
		this.nbrOfSeatsInRow = nbrOfSeatsInRow;
	}

	public int getLevelId() {
		return levelId;
	}

	public String getLevelName() {
		return levelName;
	}

	public int getSeatPrice() {
		return seatPrice;
	}

	public int getNbrOfRows() {
		return nbrOfRows;
	}

	public int getNbrOfSeatsInRow() {
		return nbrOfSeatsInRow;
	}

	public static Map<Integer, Level> getLevels() {
		return levels;
	}

	private static Map<Integer, Level> initializeLevels() {
		Map<Integer, Level> levels = new HashMap<Integer, Level>();
		for (SeatsInfra seatsinfo : SeatsInfra.values()) {
			List<Seat> seats = new ArrayList<Seat>();
			Level lvl = new Level();
			lvl.setLevelId(seatsinfo.levelId);
			lvl.setLevelPrice(seatsinfo.seatPrice);
			for (int totalSeats = 0; totalSeats < seatsinfo.nbrOfRows * seatsinfo.nbrOfSeatsInRow; totalSeats++) {
				Seat seat = new Seat();
				seat.setSeatId(totalSeats + 1);
				seats.add(seat);
			}
			lvl.setSeats(seats);
			levels.put(seatsinfo.levelId, lvl);
		}
		return levels;
	}
}
