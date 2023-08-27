package pl.teardrop.financemanager.domain.category.exception;

public class CategoryNotFoundException extends Exception {

	public CategoryNotFoundException(String message) {
		super(message);
	}

	public CategoryNotFoundException(Throwable cause) {
		super(cause);
	}
}
