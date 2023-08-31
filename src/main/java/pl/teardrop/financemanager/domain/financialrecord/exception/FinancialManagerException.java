package pl.teardrop.financemanager.domain.financialrecord.exception;

public class FinancialManagerException extends Exception {

	public FinancialManagerException(String message) {
		super(message);
	}

	public FinancialManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}
