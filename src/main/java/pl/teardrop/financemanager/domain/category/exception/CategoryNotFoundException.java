package pl.teardrop.financemanager.domain.category.exception;

import pl.teardrop.financemanager.common.exception.FinancialManagerException;

public class CategoryNotFoundException extends FinancialManagerException {

	public CategoryNotFoundException(String message) {
		super(message);
	}
}
