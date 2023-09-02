package pl.teardrop.financemanager.domain.category.dto;

import pl.teardrop.financemanager.domain.category.model.CategoryId;

public record UpdateCategoryCommand(
		CategoryId id,
		Integer priority,
		String name
) {

	public UpdateCategoryCommand(UpdateCategoryRequest request) {
		this(new CategoryId(request.id()),
			 request.priority(),
			 request.name());
	}
}
