package pl.teardrop.financemanager.repository;

import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecord;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface FinancialRecordRepository extends Repository<FinancialRecord, Long> {

	Optional<FinancialRecord> findById(Long id);

	List<FinancialRecord> findAll();

	List<FinancialRecord> findByUserOrderByCreatedAtDesc(User user);

	List<FinancialRecord> findByCategory(Category category);

	List<FinancialRecord> getByCategory(Category category);

	FinancialRecord save(FinancialRecord financialRecord);

	void deleteById(Long id);
}
