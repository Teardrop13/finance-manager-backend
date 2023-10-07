package pl.teardrop.financemanager.domain.accountingperiod.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountingPeriodRepository extends CrudRepository<AccountingPeriod, Long> {

	List<AccountingPeriod> findByUserIdOrderByStartsOn(UserId userId);

	@Query(value = "SELECT p "
				   + "FROM AccountingPeriod p "
				   + "WHERE p.userId = :userId "
				   + "AND p.startsOn <= :date "
				   + "AND p.endsOn >= :date")
	Optional<AccountingPeriod> findFirstByDate(LocalDate date, UserId userId);

}
