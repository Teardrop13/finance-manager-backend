package pl.teardrop.financemanager.domain.financialrecord.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum FinancialRecordType {
	INCOME("income"),
	EXPENSE("expense");

	@JsonValue
	private final String textValue;

	public static FinancialRecordType getByTextValue(String value) {
		return Arrays.stream(values()).filter(v -> Objects.equals(v.name(), value.toUpperCase())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Cannot find FinancialRecord type for text value %s".formatted(value)));
	}

	@Override
	public String toString() {
		return textValue;
	}
}
