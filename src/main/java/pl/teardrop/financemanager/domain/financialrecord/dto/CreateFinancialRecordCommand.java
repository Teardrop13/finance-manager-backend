package pl.teardrop.financemanager.domain.financialrecord.dto;

import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateFinancialRecordCommand(
		UserId userId,
		String description,
		BigDecimal amount,
		String category,
		FinancialRecordType type,
		LocalDate transactionDate
) {

	public CreateFinancialRecordCommand(UserId userId, CreateFinancialRecordRequest request) {
		this(userId,
			 request.description(),
			 request.amount(),
			 request.category(),
			 request.type(),
			 request.transactionDate());
	}
}
