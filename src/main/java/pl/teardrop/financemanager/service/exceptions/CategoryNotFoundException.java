package pl.teardrop.financemanager.service.exceptions;

public class CategoryNotFoundException extends Exception {

	public CategoryNotFoundException(String message) {
		super(message);
	}

	public CategoryNotFoundException(Throwable cause) {
		super(cause);
	}
}
