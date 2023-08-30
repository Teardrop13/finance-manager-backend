package pl.teardrop.financemanager.domain.financialrecord.dto;

import java.math.BigDecimal;

public record CategorySummaryDTO(BigDecimal amount,
								 String category) {
}
