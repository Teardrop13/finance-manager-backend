package pl.teardrop.financemanager.service.exceptions;

public class FinancialRecordNotFoundException extends Exception {

	public FinancialRecordNotFoundException(String message) {
		super(message);
	}

	public FinancialRecordNotFoundException(Throwable cause) {
		super(cause);
	}
}
