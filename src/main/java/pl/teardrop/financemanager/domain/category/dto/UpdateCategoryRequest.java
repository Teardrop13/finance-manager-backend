package pl.teardrop.financemanager.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(@NotNull
									Long id,
									@NotNull
									Integer priority,
									@NotBlank
									String name
) {

}
