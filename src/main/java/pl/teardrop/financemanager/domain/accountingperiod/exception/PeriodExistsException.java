package pl.teardrop.financemanager.domain.accountingperiod.exception;

public class PeriodExistsException extends RuntimeException {

	public PeriodExistsException(String message) {
		super(message);
	}

	public PeriodExistsException(Throwable cause) {
		super(cause);
	}
}
