package pl.teardrop.financemanager.domain.financialrecord.exception;

public class FinancialRecordNotFoundException extends Exception {

	public FinancialRecordNotFoundException(String message) {
		super(message);
	}

	public FinancialRecordNotFoundException(Throwable cause) {
		super(cause);
	}
}
