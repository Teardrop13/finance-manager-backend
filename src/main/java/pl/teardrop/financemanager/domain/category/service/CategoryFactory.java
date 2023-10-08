package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryFactory {

	private final CategoryRetrievingService categoryRetrievingService;

	public Category get(AddCategoryCommand command) {
		CategoryPriority priority = categoryRetrievingService.getLast(command.userId(), command.type())
				.map(category -> category.getPriority().incremented())
				.orElse(CategoryPriority.ONE);

		return Category.builder()
				.name(command.name())
				.userId(command.userId())
				.type(command.type())
				.priority(priority)
				.build();
	}
}
