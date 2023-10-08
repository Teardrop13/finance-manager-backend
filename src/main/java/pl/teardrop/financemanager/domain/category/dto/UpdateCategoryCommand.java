package pl.teardrop.financemanager.domain.category.dto;

import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;

public record UpdateCategoryCommand(
		CategoryId id,
		CategoryPriority priority,
		String name
) {

	public UpdateCategoryCommand(UpdateCategoryRequest request) {
		this(new CategoryId(request.id()),
			 new CategoryPriority(request.priority()),
			 request.name());
	}
}
