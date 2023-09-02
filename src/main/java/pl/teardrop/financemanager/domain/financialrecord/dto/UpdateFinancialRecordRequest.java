package pl.teardrop.financemanager.domain.financialrecord.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFinancialRecordRequest(
		String description,
		@NotNull
		BigDecimal amount,
		@NotBlank
		String category,
		@NotNull
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		LocalDate transactionDate
) {

}
