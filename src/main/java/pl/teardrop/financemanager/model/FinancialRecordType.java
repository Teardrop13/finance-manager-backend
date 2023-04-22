package pl.teardrop.financemanager.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum FinancialRecordType {
	INCOME("income"),
	EXPENSE("expense");

	@JsonValue
	private final String textValue;

	public static Optional<FinancialRecordType> getByName(String type) {
		return Arrays.stream(values())
				.filter(v -> Objects.equals(type.toLowerCase(), v.textValue))
				.findFirst();
	}

	@Override
	public String toString() {
		return textValue;
	}
}
