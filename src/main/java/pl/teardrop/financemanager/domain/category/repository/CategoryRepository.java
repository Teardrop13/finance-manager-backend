package pl.teardrop.financemanager.domain.category.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

	Optional<Category> findByUserIdAndTypeAndNameIgnoreCase(UserId userId, FinancialRecordType type, String name);

	List<Category> findByUserIdAndTypeOrderByPriority(UserId userId, FinancialRecordType type);
}
