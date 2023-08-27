package pl.teardrop.financemanager.domain.financialrecord.dto;

import java.math.BigDecimal;

public record SummaryDTO(BigDecimal amount,
						 String category) {

	public static final SummaryDTO ZERO = new SummaryDTO(BigDecimal.ZERO, "");

	public SummaryDTO add(SummaryDTO other) {
		return new SummaryDTO(amount.add(other.amount),
							  other.category);
	}
}
