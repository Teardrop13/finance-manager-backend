package pl.teardrop.financemanager.domain.financialrecord.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFinancialRecordCommand {

	private String description;
	@NotNull
	private BigDecimal amount;
	@NotBlank
	private String category;
	@NotNull
	private FinancialRecordType type;
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate transactionDate;

}
