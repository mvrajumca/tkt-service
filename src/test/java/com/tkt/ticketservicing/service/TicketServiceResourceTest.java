package com.tkt.ticketservicing.service;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import com.tkt.ticketservicing.core.SeatHold;
import com.tkt.ticketservicing.exception.TicketSystemException;
import com.tkt.ticketservicing.service.TicketService;
import com.tkt.ticketservicing.service.TicketServiceResource;

/**
 * @author Raju
 *
 */
public class TicketServiceResourceTest {

	private final TicketService service = new TicketServiceResource();

	@Test
	public void testNumberofSeats() throws Exception {
		assertTrue(service.numSeatsAvailable(null) > 0);
	}

	@Test
	public void testNumberofSeatsPerLevel() throws Exception {
		assertTrue(service.numSeatsAvailable(Optional.of(1)) == 1250);
	}

	@Test(expected = TicketSystemException.class)
	public void testNumberofSeatsUsingInvalidLevel() throws Exception {
		service.numSeatsAvailable(Optional.of(5));
	}

	@Test(expected = TicketSystemException.class)
	public void testfindAndHoldWithInvalidLevel() throws Exception {
		service.findAndHoldSeats(20, Optional.of(2), Optional.of(5), "test@tkt.com");
	}

	@Test
	public void testFindAndHoldWithValidLevel() throws Exception {
		SeatHold seatHold = service.findAndHoldSeats(30, Optional.of(1), Optional.of(4), "test@tkt.com");
		assertNotNull(seatHold.getHoldId());
		assertEquals(30, seatHold.getNbrOfSeats());
	}

	@Test
	public void testFindAndHoldWithValidLevelWithMultipleRequests() throws Exception {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
		ExecuteTicketService callable1 = new ExecuteTicketService(service, 250, 1, 4, "test1@tkt.com");
		ScheduledFuture<Object> future1 = scheduledExecutorService.schedule(callable1, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable2 = new ExecuteTicketService(service, 400, 1, 4, "test2@tkt.com");
		ScheduledFuture<Object> future2 = scheduledExecutorService.schedule(callable2, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable3 = new ExecuteTicketService(service, 300, 1, 4, "test3@tkt.com");
		ScheduledFuture<Object> future3 = scheduledExecutorService.schedule(callable3, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable4 = new ExecuteTicketService(service, 250, 1, 4, "test4@tkt.com");
		ScheduledFuture<Object> future4 = scheduledExecutorService.schedule(callable4, 0, TimeUnit.MILLISECONDS);

		SeatHold hold1 = (SeatHold) future1.get();
		SeatHold hold2 = (SeatHold) future2.get();
		SeatHold hold3 = (SeatHold) future3.get();
		SeatHold hold4 = (SeatHold) future4.get();

		assertNotNull(hold1.getHoldId());
		assertNotNull(hold2.getHoldId());
		assertNotNull(hold3.getHoldId());
		assertNotNull(hold4.getHoldId());

		assertEquals(250, hold1.getNbrOfSeats());
		assertEquals(400, hold2.getNbrOfSeats());
		assertEquals(300, hold3.getNbrOfSeats());
		assertEquals(250, hold4.getNbrOfSeats());
	}

	@Test
	public void testFindAndHoldWithASpecifiLevelAndWithMultipleRequests() {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
		ExecuteTicketService callable1 = new ExecuteTicketService(service, 1300, 4, 4, "test1@tkt.com");
		ScheduledFuture<Object> future1 = scheduledExecutorService.schedule(callable1, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable2 = new ExecuteTicketService(service, 400, 4, 4, "test2@tkt.com");
		ScheduledFuture<Object> future2 = scheduledExecutorService.schedule(callable2, 0, TimeUnit.MILLISECONDS);

		SeatHold hold1 = null;
		try {
			hold1 = (SeatHold) future1.get();
		} catch (Exception ex) {
			fail("Hold 1 should not reach here...");
		}
		assertNotNull(hold1.getHoldId());
		assertEquals(1300, hold1.getNbrOfSeats());
		try {
			future2.get();
			fail("Hold 2 should not reach here...");
		} catch (Exception tse) {
			assertTrue(tse.getMessage().equals("com.tkt.ticketservicing.exception.TicketSystemException"));
		}
	}

	/**
	 * This is a manual test scenario to make each thread requests the max
	 * number of seats.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testManualFindAndHoldWithValidLevelWithMultipleRequests() throws Exception {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
		ExecuteTicketService callable1 = new ExecuteTicketService(service, 1250, 1, 4, "test1@tkt.com");
		ScheduledFuture<Object> future1 = scheduledExecutorService.schedule(callable1, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable2 = new ExecuteTicketService(service, 2000, 1, 4, "test2@tkt.com");
		ScheduledFuture<Object> future2 = scheduledExecutorService.schedule(callable2, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable3 = new ExecuteTicketService(service, 1300, 1, 4, "test3@tkt.com");
		ScheduledFuture<Object> future3 = scheduledExecutorService.schedule(callable3, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable4 = new ExecuteTicketService(service, 200, 1, 4, "test4@tkt.com");
		ScheduledFuture<Object> future4 = scheduledExecutorService.schedule(callable4, 0, TimeUnit.MILLISECONDS);

		SeatHold hold1 = (SeatHold) future1.get();
		SeatHold hold2 = (SeatHold) future2.get();
		SeatHold hold3 = (SeatHold) future3.get();
		SeatHold hold4 = (SeatHold) future4.get();

		assertNotNull(hold1.getHoldId());
		assertNotNull(hold2.getHoldId());
		assertNotNull(hold3.getHoldId());
		assertNotNull(hold4.getHoldId());

		assertEquals(1250, hold1.getNbrOfSeats());
		assertEquals(2000, hold2.getNbrOfSeats());
		assertEquals(1300, hold3.getNbrOfSeats());
		assertEquals(200, hold4.getNbrOfSeats());
	}

	@Test(expected = TicketSystemException.class)
	public void testFindAndHoldWithValidLevelWithMaxSeatsRequest() throws Exception {
		service.findAndHoldSeats(4000, Optional.of(1), Optional.of(4), "test@tkt.com");
	}

	@Test
	public void testFindAndHoldWithValidLevelAndReserveSeats() throws Exception {
		SeatHold seatHold = service.findAndHoldSeats(5, Optional.of(1), Optional.of(4), "test@tkt.com");
		String reserveId = service.reserveSeats(seatHold.getHoldId(), "test@tkt.com");
		assertEquals(seatHold.getHoldId(), reserveId);
	}

	@Test
	public void testFindAndHoldWithValidLevelWithMultipleRequestsAndReserveSeats() throws Exception {
		ScheduledExecutorService scheduledExecutorService1 = Executors.newScheduledThreadPool(4);
		ExecuteTicketService callable1 = new ExecuteTicketService(service, 150, 1, 4, "test1@tkt.com");
		ScheduledFuture<Object> future1 = scheduledExecutorService1.schedule(callable1, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable2 = new ExecuteTicketService(service, 180, 1, 4, "test2@tkt.com");
		ScheduledFuture<Object> future2 = scheduledExecutorService1.schedule(callable2, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable3 = new ExecuteTicketService(service, 120, 1, 4, "test3@tkt.com");
		ScheduledFuture<Object> future3 = scheduledExecutorService1.schedule(callable3, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketService callable4 = new ExecuteTicketService(service, 175, 1, 4, "test4@tkt.com");
		ScheduledFuture<Object> future4 = scheduledExecutorService1.schedule(callable4, 0, TimeUnit.MILLISECONDS);

		SeatHold hold1 = (SeatHold) future1.get();
		SeatHold hold2 = (SeatHold) future2.get();
		SeatHold hold3 = (SeatHold) future3.get();
		SeatHold hold4 = (SeatHold) future4.get();

		assertNotNull(hold1.getHoldId());
		assertNotNull(hold2.getHoldId());
		assertNotNull(hold3.getHoldId());
		assertNotNull(hold4.getHoldId());

		assertEquals(150, hold1.getNbrOfSeats());
		assertEquals(180, hold2.getNbrOfSeats());
		assertEquals(120, hold3.getNbrOfSeats());
		assertEquals(175, hold4.getNbrOfSeats());

		ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(4);
		ExecuteTicketReserveService call1 = new ExecuteTicketReserveService(service, hold1.getHoldId(),
				"test1@tkt.com");
		ScheduledFuture<Object> fut1 = scheduledExecutorService2.schedule(call1, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketReserveService call2 = new ExecuteTicketReserveService(service, hold2.getHoldId(),
				"test2@tkt.com");
		ScheduledFuture<Object> fut2 = scheduledExecutorService2.schedule(call2, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketReserveService call3 = new ExecuteTicketReserveService(service, hold3.getHoldId(),
				"test3@tkt.com");
		ScheduledFuture<Object> fut3 = scheduledExecutorService2.schedule(call3, 0, TimeUnit.MILLISECONDS);
		ExecuteTicketReserveService call4 = new ExecuteTicketReserveService(service, hold4.getHoldId(),
				"test4@tkt.com");
		ScheduledFuture<Object> fut4 = scheduledExecutorService2.schedule(call4, 0, TimeUnit.MILLISECONDS);

		String confirmationNumber1 = (String) fut1.get();
		String confirmationNumber2 = (String) fut2.get();
		String confirmationNumber3 = (String) fut3.get();
		String confirmationNumber4 = (String) fut4.get();

		assertNotNull(confirmationNumber1);
		assertNotNull(confirmationNumber2);
		assertNotNull(confirmationNumber3);
		assertNotNull(confirmationNumber4);

		assertEquals(hold1.getHoldId(), confirmationNumber1);
		assertEquals(hold2.getHoldId(), confirmationNumber2);
		assertEquals(hold3.getHoldId(), confirmationNumber3);
		assertEquals(hold4.getHoldId(), confirmationNumber4);
	}

	@Test(expected = TicketSystemException.class)
	public void testFindAndHoldWithValidLevelAndReserveSeatsAfterHoldtime() throws Exception {
		SeatHold seatHold = service.findAndHoldSeats(5, Optional.of(1), Optional.of(4), "test@tkt.com");
		sleep(3000);
		service.reserveSeats(seatHold.getHoldId(), "test@tkt.com");
}

	private static class ExecuteTicketService implements Callable<Object> {

		private final TicketService ticketService;

		private int numSeats;

		private int minLevel;

		private int maxLevel;

		private String customerEmail;

		public ExecuteTicketService(TicketService ticketService, int numSeats, int minLevel, int maxLevel,
				String customerEmail) {
			this.ticketService = ticketService;
			this.numSeats = numSeats;
			this.customerEmail = customerEmail;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
		}

		public Object call() throws Exception {
			try {
				return ticketService.findAndHoldSeats(numSeats, Optional.of(minLevel), Optional.of(maxLevel),
						customerEmail);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private static class ExecuteTicketReserveService implements Callable<Object> {

		private final TicketService ticketService;

		private String holdId;

		private String customerEmail;

		public ExecuteTicketReserveService(TicketService ticketService, String holdId, String customerEmail) {
			this.ticketService = ticketService;
			this.customerEmail = customerEmail;
			this.holdId = holdId;
		}

		public Object call() throws Exception {
			try {
				return ticketService.reserveSeats(holdId, customerEmail);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
