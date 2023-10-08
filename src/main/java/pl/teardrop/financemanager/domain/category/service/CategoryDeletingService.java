package pl.teardrop.financemanager.domain.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.model.CategoryId;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryDeletingService {

	private final CategoryService categoryService;
	private final CategoriesReorderingService categoriesReorderingService;

	@PreAuthorize("hasPermission(#categoryId, 'Category', 'delete')")
	public void delete(CategoryId categoryId) {
		categoryService.getById(categoryId).ifPresent(category -> {
			category.setDeleted(true);
			categoryService.save(category);
			log.info("Category marked deleted id={}, userId={}", category.getId(), category.getUserId().getId());
			categoriesReorderingService.reorder(category.getUserId(), category.getType());
		});
	}

}
