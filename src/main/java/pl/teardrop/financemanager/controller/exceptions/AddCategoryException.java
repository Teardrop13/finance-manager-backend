package pl.teardrop.financemanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
		value = HttpStatus.CONFLICT,
		reason = "Could not add category."
)
public class AddCategoryException extends RuntimeException {

	public AddCategoryException(String message) {
		super(message);
	}

	public AddCategoryException(Throwable cause) {
		super(cause);
	}
}
