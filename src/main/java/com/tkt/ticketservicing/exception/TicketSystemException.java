
package com.tkt.ticketservicing.exception;

/**
 * @author Raju
 *
 */
public class TicketSystemException extends Exception {

	private static final long serialVersionUID = 1L;

	public String message;

	public TicketSystemException(String message) {
		this.message = message;
	}

	public TicketSystemException(Throwable cause, String message) {
		super(cause);
		this.message = message;
	}
}
