package pl.teardrop.financemanager.domain.financialrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordId;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UpdateFinancialRecordCommand {

	private final FinancialRecordId recordId;
	private final String description;
	private final BigDecimal amount;
	private final String category;
	private final LocalDate transactionDate;

	public UpdateFinancialRecordCommand(FinancialRecordId recordId, UpdateFinancialRecordRequest request) {
		this(recordId,
			 request.getDescription(),
			 request.getAmount(),
			 request.getCategory(),
			 request.getTransactionDate());
	}
}
