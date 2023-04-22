package pl.teardrop.financemanager.configuration;

import org.springframework.core.convert.converter.Converter;
import pl.teardrop.financemanager.model.FinancialRecordType;

public class StringToFinancialRecordTypeConverter implements Converter<String, FinancialRecordType> {

	@Override
	public FinancialRecordType convert(String source) {
		return FinancialRecordType.valueOf(source.toUpperCase());
	}
}
