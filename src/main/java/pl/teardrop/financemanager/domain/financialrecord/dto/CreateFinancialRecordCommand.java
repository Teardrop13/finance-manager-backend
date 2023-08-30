package pl.teardrop.financemanager.domain.financialrecord.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor()
public class CreateFinancialRecordCommand {

	private final UserId userId;
	private final String description;
	private final BigDecimal amount;
	private final String category;
	private final FinancialRecordType type;
	private final LocalDate transactionDate;

	public CreateFinancialRecordCommand(UserId userId, CreateFinancialRecordRequest request) {
		this(userId,
			 request.getDescription(),
			 request.getAmount(),
			 request.getCategory(),
			 request.getType(),
			 request.getTransactionDate());
	}
}
