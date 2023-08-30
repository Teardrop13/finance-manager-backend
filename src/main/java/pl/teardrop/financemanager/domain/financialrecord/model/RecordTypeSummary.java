package pl.teardrop.financemanager.domain.financialrecord.model;

import pl.teardrop.financemanager.domain.financialrecord.dto.RecordTypeSummaryDTO;

import java.math.BigDecimal;

public record RecordTypeSummary(BigDecimal amount, FinancialRecordType type) {

	public static final RecordTypeSummary ZERO = new RecordTypeSummary(BigDecimal.ZERO, null);

	public RecordTypeSummary add(RecordTypeSummary other) {
		return new RecordTypeSummary(amount.add(other.amount),
									 other.type);
	}

	public RecordTypeSummaryDTO toDto() {
		return new RecordTypeSummaryDTO(amount, type.getTextValue());
	}
}
