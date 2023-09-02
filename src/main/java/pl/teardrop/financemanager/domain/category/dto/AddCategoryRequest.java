package pl.teardrop.financemanager.domain.category.dto;

import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

public record AddCategoryRequest(
		String name,
		FinancialRecordType type
) {

}
