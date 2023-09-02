package pl.teardrop.financemanager.domain.financialrecord.exception;

import pl.teardrop.financemanager.common.exception.FinancialManagerException;

public class FinancialRecordNotFoundException extends FinancialManagerException {

	public FinancialRecordNotFoundException(String message) {
		super(message);
	}
}
