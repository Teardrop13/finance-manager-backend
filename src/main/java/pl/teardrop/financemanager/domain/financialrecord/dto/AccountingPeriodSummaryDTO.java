package pl.teardrop.financemanager.domain.financialrecord.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountingPeriodSummaryDTO(BigDecimal income,
										 BigDecimal expense,
										 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
										 LocalDate startsOn,
										 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
										 LocalDate endsOn,
										 Long accountingPeriodId
) {

}
