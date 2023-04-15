package pl.teardrop.financemanager.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        reason = "Requested record does not exist"
)
public class FinancialRecordNotFoundException extends RuntimeException {
    public FinancialRecordNotFoundException(String message) {
        super(message);
    }

    public FinancialRecordNotFoundException(Throwable cause) {
        super(cause);
    }
}
