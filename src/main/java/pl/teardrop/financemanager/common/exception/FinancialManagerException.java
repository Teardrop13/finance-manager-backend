package pl.teardrop.financemanager.common.exception;

public class FinancialManagerException extends Exception {

	public FinancialManagerException(String message) {
		super(message);
	}

	public FinancialManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}
