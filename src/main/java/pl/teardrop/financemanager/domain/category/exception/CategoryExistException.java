package pl.teardrop.financemanager.domain.category.exception;

import pl.teardrop.financemanager.common.exception.FinancialManagerException;

public class CategoryExistException extends FinancialManagerException {

	public CategoryExistException(String message) {
		super(message);
	}
}
