package io.github.teardrop13.financemanager.repositories;

import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.financemanager.domain.category.Category;
import io.github.teardrop13.financemanager.domain.record.FinancialRecord;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface RecordRepository extends Repository<FinancialRecord, Long> {

	Optional<FinancialRecord> findById(Long id);

	List<FinancialRecord> findAll();

	List<FinancialRecord> findByUserOrderByCreatedAtDesc(User user);

	List<FinancialRecord> findByCategory(Category category);

	List<FinancialRecord> getByCategory(Category category);

	FinancialRecord save(FinancialRecord record);

	void deleteById(Long id);
}
