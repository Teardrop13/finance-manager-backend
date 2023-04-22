package pl.teardrop.financemanager.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecord;

import java.util.List;
import java.util.Optional;

@Component
public interface FinancialRecordRepository extends Repository<FinancialRecord, Long> {

	@PostAuthorize("returnObject.isPresent() && returnObject.get().getUser().getId() == authentication.principal.id")
	Optional<FinancialRecord> findById(Long id);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<FinancialRecord> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	@PreAuthorize("#category.getUser().getId() == authentication.principal.id")
	List<FinancialRecord> findByCategory(Category category);

	FinancialRecord save(FinancialRecord financialRecord);

	void delete(FinancialRecord financialRecord);
}
