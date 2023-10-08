package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.CategoryExistException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddingCategoryService {

	private final CategoryRetrievingService categoryRetrievingService;
	private final CategorySavingService categorySavingService;
	private final CategoryFactory categoryFactory;

	public Category add(AddCategoryCommand command) throws CategoryExistException {
		Optional<Category> categoryOpt = categoryRetrievingService.getByUserAndTypeAndName(command.userId(), command.type(), command.name());

		if (categoryOpt.isPresent()) {
			Category existingCategory = categoryOpt.get();
			log.info("Category with name \"{}\" exists, categoryId={}", command.name(), existingCategory.getId());

			if (existingCategory.isDeleted()) {
				log.info("Category id={} was deleted before, setting deleted=false", existingCategory.getId());

				CategoryPriority priority = categoryRetrievingService.getLast(existingCategory.getUserId(), existingCategory.getType())
						.map(category -> category.getPriority().incremented())
						.orElse(CategoryPriority.ONE);

				existingCategory.setDeleted(false);
				existingCategory.setName(command.name());
				existingCategory.setPriority(priority);

				return categorySavingService.save(existingCategory);
			} else {
				throw new CategoryExistException("Category with name \"%s\" exists".formatted(command.name()));
			}
		} else {
			Category category = categoryFactory.get(command);
			return categorySavingService.save(category);
		}
	}
}
