package pl.teardrop.financemanager.domain.category.dto;

import java.util.List;

public record ReorderCategoriesRequest(
		List<ReorderCategoryRequest> reorderCategoryRequests
) {

}
