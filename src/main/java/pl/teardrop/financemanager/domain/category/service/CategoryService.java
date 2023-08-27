package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.common.JsonUtil;
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

	public List<Category> getByUser(UserId userId) {
		return categoryRepository.findByUserIdOrderByPriority(userId).stream()
				.filter(c -> !c.isDeleted())
				.toList();
	}

	public Optional<Category> getById(CategoryId categoryId) {
		return categoryRepository.findById(categoryId.getId());
	}

	public Category save(Category category) {
		Category savedCategory = categoryRepository.save(category);
		log.info("Saved category id={}, userId={}", savedCategory.getId(), savedCategory.getUserId().getId());
		return savedCategory;
	}

	public void delete(CategoryId categoryId) {
		categoryRepository.findById(categoryId.getId()).ifPresent(category -> {
			category.setDeleted(true);
			categoryRepository.save(category);
			log.info("Category marked deleted id={}, userId={}", category.getId(), category.getUserId().getId());
		});
	}

	public List<Category> getByUserAndType(UserId userId, FinancialRecordType type) {
		return categoryRepository.getByUserIdAndTypeOrderByPriority(userId, type);
	}

	public void addDefaultCategorires(UserId userId) {
		new JsonUtil().loadDefaultCategories().forEach(category -> {
			category.setUserId(userId);
			save(category);
		});
		log.info("Added default categories for userId={}", userId.getId());
	}

	public Optional<Category> getLast(UserId userId, FinancialRecordType type) {
		return getByUserAndType(userId, type).stream()
				.max(Comparator.comparing(Category::getPriority));
	}

	public Optional<Category> getByUserAndTypeAndName(UserId userId, FinancialRecordType type, String name) {
		return categoryRepository.getByUserIdAndTypeAndNameIgnoreCase(userId, type, name);
	}

	public void reorder(UserId userId, FinancialRecordType type) {
		List<Category> categories = getByUserAndType(userId, type);
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
			category.setPriority(i + 1);
			save(category);
		}
		log.info("Categories reordered for userId={}", userId.getId());
	}
}
