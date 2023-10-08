package pl.teardrop.financemanager.domain.category.dto;

import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;

public record ReorderCategoryCommand(
		CategoryId id,
		CategoryPriority priority
) {

	public ReorderCategoryCommand(ReorderCategoryRequest request) {
		this(new CategoryId(request.id()), new CategoryPriority(request.priority()));
	}
}
