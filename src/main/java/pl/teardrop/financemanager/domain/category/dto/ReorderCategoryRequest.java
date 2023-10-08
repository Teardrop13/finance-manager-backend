package pl.teardrop.financemanager.domain.category.dto;

import jakarta.validation.constraints.NotNull;

public record ReorderCategoryRequest(@NotNull
									 Long id,
									 @NotNull
									 Integer priority
) {

}
