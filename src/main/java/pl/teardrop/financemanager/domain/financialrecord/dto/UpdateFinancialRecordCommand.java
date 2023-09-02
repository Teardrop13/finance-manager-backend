package pl.teardrop.financemanager.domain.financialrecord.dto;

import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordId;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFinancialRecordCommand(
		FinancialRecordId recordId,
		String description,
		BigDecimal amount,
		String category,
		LocalDate transactionDate
) {

	public UpdateFinancialRecordCommand(FinancialRecordId recordId, UpdateFinancialRecordRequest request) {
		this(recordId,
			 request.description(),
			 request.amount(),
			 request.category(),
			 request.transactionDate());
	}
}
