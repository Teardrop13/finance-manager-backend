package pl.teardrop.financemanager.domain.financialrecord.exception;

import pl.teardrop.financemanager.common.exception.FinancialManagerException;

public class AccountingPeriodNotFoundException extends FinancialManagerException {

	public AccountingPeriodNotFoundException(String message) {
		super(message);
	}
}
