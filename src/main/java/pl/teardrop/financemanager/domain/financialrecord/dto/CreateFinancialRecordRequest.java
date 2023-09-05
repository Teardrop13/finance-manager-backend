package pl.teardrop.financemanager.domain.financialrecord.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateFinancialRecordRequest(
		String description,
		@NotNull @Min(0)
		BigDecimal amount,
		@NotBlank
		String category,
		@NotNull
		FinancialRecordType type,
		@NotNull
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		LocalDate transactionDate
) {

}
