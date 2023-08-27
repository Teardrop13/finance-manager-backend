package pl.teardrop.financemanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.teardrop.financemanager.model.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordDTO {

	private Long id;
	private String description;
	private BigDecimal amount;
	private String category;
	private FinancialRecordType type;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate transactionDate;

}
