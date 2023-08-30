package pl.teardrop.financemanager.domain.financialrecord.dto;

import java.math.BigDecimal;

public record RecordTypeSummaryDTO(BigDecimal amount,
								   String type) {

}
