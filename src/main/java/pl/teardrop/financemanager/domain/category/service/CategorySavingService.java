package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategorySavingService {

	private final CategoryRetrievingService categoryRetrievingService;
	private final CategoryRepository categoryRepository;

	@PreAuthorize("#category.isNew() || hasPermission(#category.categoryId(), 'Category', 'write')")
	public Category save(Category category) {
		Category savedCategory = categoryRepository.save(category);
		log.info("Saved category id={}, userId={}", savedCategory.getId(), savedCategory.getUserId().getId());
		return savedCategory;
	}

	// todo unused?
	@PreAuthorize("hasPermission(#command.id, 'Category', 'write')")
	public void update(UpdateCategoryCommand command) throws CategoryNotFoundException {
		Category category = categoryRetrievingService.getById(command.id())
				.orElseThrow(() -> new CategoryNotFoundException("Category id=%d not found".formatted(command.id().getId())));

		category.setName(command.name());
		category.setPriority(command.priority());

		save(category);

		log.info("Category id={} updated", command.id().getId());
	}
}
