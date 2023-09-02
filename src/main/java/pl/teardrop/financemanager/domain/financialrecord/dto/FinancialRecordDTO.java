package pl.teardrop.financemanager.domain.financialrecord.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialRecordDTO(
		Long id,
		String description,
		BigDecimal amount,
		String category,
		FinancialRecordType type,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		LocalDate transactionDate
) {

}
