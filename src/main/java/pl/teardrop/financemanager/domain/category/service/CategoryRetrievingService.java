package pl.teardrop.financemanager.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.domain.UserId;
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
public class CategoryRetrievingService {

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


}
