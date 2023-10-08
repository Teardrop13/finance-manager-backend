package pl.teardrop.financemanager.domain.category.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoriesCommand;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriesReorderingValidator {

	private final CategoryService categoryService;

	public boolean validate(ReorderCategoriesCommand command) {
		List<Category> categories = categoryService.getNotDeletedByUserAndType(command.userId(), command.type());

		Set<CategoryId> ids = categories.stream()
				.map(Category::categoryId)
				.collect(Collectors.toSet());

		Set<CategoryPriority> priorities = categories.stream()
				.map(Category::getPriority)
				.collect(Collectors.toSet());

		command.reorderCategoryCommands().forEach(c -> ids.remove(c.id()));
		command.reorderCategoryCommands().forEach(c -> priorities.remove(c.priority()));

		return ids.isEmpty() && priorities.isEmpty();
	}

}
