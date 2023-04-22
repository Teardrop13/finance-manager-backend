package pl.teardrop.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private String type;
	private LocalDate transactionDate;

}
