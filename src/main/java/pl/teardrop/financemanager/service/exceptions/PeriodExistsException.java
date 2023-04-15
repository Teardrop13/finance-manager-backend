package pl.teardrop.financemanager.service.exceptions;

public class PeriodExistsException extends RuntimeException {

	public PeriodExistsException(String message) {
		super(message);
	}

	public PeriodExistsException(Throwable cause) {
		super(cause);
	}
}
