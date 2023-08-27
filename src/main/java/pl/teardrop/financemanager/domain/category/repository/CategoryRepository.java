package pl.teardrop.financemanager.domain.category.repository;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@Component
public interface CategoryRepository extends Repository<Category, Long> {

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUser().getId() == authentication.principal.id : true")
	Optional<Category> findById(Long id);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<Category> findByUserOrderByPriority(User user);

	Category save(Category category);

	@PreAuthorize("#category.getUser().getId() == authentication.principal.id")
	void delete(Category category);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<Category> findByUserAndType(User user, FinancialRecordType type);

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUser().getId() == authentication.principal.id : true")
	Optional<Category> getByUserAndTypeAndNameIgnoreCase(User user, FinancialRecordType type, String name);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<Category> getByUserAndTypeOrderByPriority(User user, FinancialRecordType type);
}
