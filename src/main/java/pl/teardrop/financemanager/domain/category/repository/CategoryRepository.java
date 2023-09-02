package pl.teardrop.financemanager.domain.category.repository;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CategoryRepository extends Repository<Category, Long> {

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUserId().getId() == authentication.principal.id : true")
	Optional<Category> findById(Long id);

	@PreAuthorize("#userId.getId() == authentication.principal.id")
	List<Category> findByUserIdOrderByPriority(UserId userId);

	Category save(Category category);

	@PreAuthorize("#category.getUserId().getId() == authentication.principal.id")
	void delete(Category category);

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUserId().getId() == authentication.principal.id : true")
	Optional<Category> getByUserIdAndTypeAndNameIgnoreCase(UserId userId, FinancialRecordType type, String name);

	@PreAuthorize("#userId.getId() == authentication.principal.id")
	List<Category> getByUserIdAndTypeOrderByPriority(UserId userId, FinancialRecordType type);
}
