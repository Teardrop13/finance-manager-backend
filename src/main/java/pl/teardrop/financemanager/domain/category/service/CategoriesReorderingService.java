package pl.teardrop.financemanager.domain.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoriesCommand;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.InvalidReorderCategoryCommandException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CategoriesReorderingService {

	private final CategoryService categoryService;
	private final CategoriesReorderingValidator validator;

	@Transactional
	public void reorder(ReorderCategoriesCommand reorderCategoriesCommand) throws InvalidReorderCategoryCommandException {
		if (!validator.validate(reorderCategoriesCommand)) {
			throw new InvalidReorderCategoryCommandException();
		}

		for (ReorderCategoryCommand command : reorderCategoriesCommand.reorderCategoryCommands()) {
			Category category = categoryService.getById(command.id())
					.orElseThrow(() -> new RuntimeException("Category with id %d does not exist".formatted(command.id().getId())));
			category.setPriority(command.priority());
			categoryService.save(category);
		}
	}

	@Transactional
	public void reorder(UserId userId, FinancialRecordType type) {
		List<Category> categories = categoryService.getNotDeletedByUserAndType(userId, type);

		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
			category.setPriority(new CategoryPriority(i + 1));
			categoryService.save(category);
		}

		log.info("Categories reordered for userId={}", userId.getId());
	}
}
