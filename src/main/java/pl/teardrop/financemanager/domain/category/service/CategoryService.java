package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.CategoryExistException;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@PreAuthorize("hasPermission(#categoryId, 'Category', 'read')")
	public Optional<Category> getById(CategoryId categoryId) {
		return categoryRepository.findById(categoryId.getId());
	}

	public Optional<Category> getLast(UserId userId, FinancialRecordType type) {
		return getNotDeletedByUserAndType(userId, type).stream()
				.max(Comparator.comparing(Category::getPriority));
	}

	public Optional<Category> getByUserAndTypeAndName(UserId userId, FinancialRecordType type, String name) {
		return categoryRepository.findByUserIdAndTypeAndNameIgnoreCase(userId, type, name);
	}

	public List<Category> getNotDeletedByUserAndType(UserId userId, FinancialRecordType type) {
		return categoryRepository.findByUserIdAndTypeOrderByPriority(userId, type).stream()
				.filter(category -> !category.isDeleted())
				.toList();
	}

	public List<Category> getByUserAndType(UserId userId, FinancialRecordType type) {
		return categoryRepository.findByUserIdAndTypeOrderByPriority(userId, type);
	}

	public Category create(AddCategoryCommand command) throws CategoryExistException {
		Optional<Category> categoryOpt = getByUserAndTypeAndName(command.userId(), command.type(), command.name());

		if (categoryOpt.isPresent()) {
			Category existingCategory = categoryOpt.get();
			log.info("Category with name \"{}\" exists, categoryId={}", command.name(), existingCategory.getId());

			if (existingCategory.isDeleted()) {
				log.info("Category id={} was deleted before, setting deleted=false", existingCategory.getId());

				Integer priority = getLast(existingCategory.getUserId(), existingCategory.getType())
						.map(category -> category.getPriority() + 1)
						.orElse(1);

				existingCategory.setDeleted(false);
				existingCategory.setName(command.name());
				existingCategory.setPriority(priority);

				return save(existingCategory);
			} else {
				throw new CategoryExistException("Category with name \"%s\" exists".formatted(command.name()));
			}
		} else {
			Integer priority = getLast(command.userId(), command.type())
					.map(category -> category.getPriority() + 1)
					.orElse(1);

			Category category = Category.builder()
					.name(command.name())
					.userId(command.userId())
					.type(command.type())
					.priority(priority)
					.build();

			return save(category);
		}
	}

	@PreAuthorize("#category.isNew() || hasPermission(#category.categoryId(), 'Category', 'write')")
	public Category save(Category category) {
		Category savedCategory = categoryRepository.save(category);
		log.info("Saved category id={}, userId={}", savedCategory.getId(), savedCategory.getUserId().getId());
		return savedCategory;
	}

	@PreAuthorize("hasPermission(#categoryId, 'Category', 'delete')")
	public void delete(CategoryId categoryId) {
		getById(categoryId).ifPresent(category -> {
			category.setDeleted(true);
			save(category);
			log.info("Category marked deleted id={}, userId={}", category.getId(), category.getUserId().getId());
			reorder(category.getUserId(), category.getType());
		});
	}

	public void reorder(UserId userId, FinancialRecordType type) {
		List<Category> categories = getNotDeletedByUserAndType(userId, type);
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
			category.setPriority(i + 1);
			save(category);
		}
		log.info("Categories reordered for userId={}", userId.getId());
	}

	@PreAuthorize("hasPermission(#command.id, 'Category', 'write')")
	public void update(UpdateCategoryCommand command) throws CategoryNotFoundException {
		Category category = getById(command.id())
				.orElseThrow(() -> new CategoryNotFoundException("Category id=%d not found".formatted(command.id().getId())));

		category.setName(command.name());
		category.setPriority(command.priority());

		save(category);

		log.info("Category id={} updated", command.id().getId());
	}
}
