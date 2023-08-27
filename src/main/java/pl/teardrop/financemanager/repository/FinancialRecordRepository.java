package pl.teardrop.financemanager.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@Component
public interface FinancialRecordRepository extends Repository<FinancialRecord, Long> {

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUser().getId() == authentication.principal.id : true")
	Optional<FinancialRecord> findById(Long id);

	@Query(value = "SELECT r "
				   + "FROM FinancialRecord r "
				   + "LEFT JOIN FETCH r.category "
				   + "WHERE r.accountingPeriod.id = :periodId "
				   + "AND r.type = :type")
	List<FinancialRecord> findByPeriodIdAndType(int periodId, FinancialRecordType type);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<FinancialRecord> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<FinancialRecord> findByUserAndAccountingPeriodIdAndType(User user,
																 int accountingPeriodId,
																 FinancialRecordType type,
																 Pageable pageable);

	@PreAuthorize("#category.getUser().getId() == authentication.principal.id")
	List<FinancialRecord> findByCategory(Category category);

	FinancialRecord save(FinancialRecord financialRecord);

	@PreAuthorize("#financialRecord.getUser().getId() == authentication.principal.id")
	void delete(FinancialRecord financialRecord);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	int countByUser(User user);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	int countByUserAndAccountingPeriodIdAndType(User user, int accountingPeriodId, FinancialRecordType type);
}
