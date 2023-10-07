package pl.teardrop.financemanager.domain.category.dto;

import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

public record AddCategoryCommand(UserId userId,
								 String name,
								 FinancialRecordType type) {

	public AddCategoryCommand(UserId userId, AddCategoryRequest request) {
		this(userId,
			 request.name(),
			 request.type());
	}
}
