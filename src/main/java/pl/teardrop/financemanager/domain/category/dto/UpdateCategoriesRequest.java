package pl.teardrop.financemanager.domain.category.dto;

import java.util.List;

public record UpdateCategoriesRequest(
		List<UpdateCategoryRequest> updateCategoryRequests
) {

}
