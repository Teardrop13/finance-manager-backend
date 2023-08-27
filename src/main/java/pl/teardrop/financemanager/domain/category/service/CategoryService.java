package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;
import pl.teardrop.financemanager.common.JsonUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<Category> getByUser(User user) {
		return categoryRepository.findByUserOrderByPriority(user).stream()
				.filter(c -> !c.isDeleted())
				.toList();
	}

	public Optional<Category> getById(long id) {
		return categoryRepository.findById(id);
	}

	public Category save(Category category) {
		Category savedCategory = categoryRepository.save(category);
		log.info("Saved category id={}, userId={}", savedCategory.getId(), savedCategory.getUser().getId());
		return savedCategory;
	}

	public void delete(long id) {
		categoryRepository.findById(id).ifPresent(category -> {
			category.setDeleted(true);
			categoryRepository.save(category);
			log.info("Category marked deleted id={}, userId={}", category.getId(), category.getUser().getId());
		});
	}

	public List<Category> getByUserAndType(User user, FinancialRecordType type) {
		return categoryRepository.getByUserAndTypeOrderByPriority(user, type);
	}

	public void addDefaultCategorires(User user) {
		new JsonUtil().loadDefaultCategories().forEach(category -> {
			category.setUser(user);
			save(category);
		});
		log.info("Added default categories for user id={}", user.getId());
	}

	public List<Category> getByUser(User user, FinancialRecordType type) {
		return categoryRepository.findByUserAndType(user, type);
	}

	public Optional<Category> getLast(User user, FinancialRecordType type) {
		return getByUser(user, type).stream()
				.max(Comparator.comparing(Category::getPriority));
	}

	public Optional<Category> getByUserAndTypeAndName(User user, FinancialRecordType type, String name) {
		return categoryRepository.getByUserAndTypeAndNameIgnoreCase(user, type, name);
	}

	public void reorder(User user, FinancialRecordType type) {
		List<Category> categories = getByUserAndType(user, type);
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
			category.setPriority(i + 1);
			save(category);
		}
		log.info("Categories reordered for user id={}", user.getId());
	}
}
