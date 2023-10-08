package pl.teardrop.financemanager.domain.category.dto;

import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;

public record ReorderCategoriesCommand(
		UserId userId,
		FinancialRecordType type,
		List<ReorderCategoryCommand> reorderCategoryCommands
) {

	public ReorderCategoriesCommand(UserId userId, FinancialRecordType type, ReorderCategoriesRequest request) {
		this(userId,
			 type,
			 request.reorderCategoryRequests().stream().map(ReorderCategoryCommand::new).toList());
	}
}
