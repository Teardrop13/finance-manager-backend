package pl.teardrop.financemanager.domain.financialrecord.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FinancialRecordType {
	INCOME("income"),
	EXPENSE("expense");

	@JsonValue
	private final String textValue;

	@Override
	public String toString() {
		return textValue;
	}
}
