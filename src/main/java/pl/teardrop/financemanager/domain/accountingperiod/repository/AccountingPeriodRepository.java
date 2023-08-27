package pl.teardrop.financemanager.domain.accountingperiod.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public interface AccountingPeriodRepository extends Repository<AccountingPeriod, Long> {

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUser().getId() == authentication.principal.id : true")
	Optional<AccountingPeriod> findById(Long id);

	@PreAuthorize("#user.getId() == authentication.principal.id")
	List<AccountingPeriod> findByUserOrderByStartsOn(User user);

	AccountingPeriod save(AccountingPeriod accountingPeriod);

	@PreAuthorize("#accountingPeriod.getUser().getId() == authentication.principal.id")
	void delete(AccountingPeriod accountingPeriod);

	@Query(value = "SELECT p "
				   + "FROM AccountingPeriod p "
				   + "WHERE p.user = :user "
				   + "AND p.startsOn <= :date "
				   + "AND p.endsOn >= :date")
	Optional<AccountingPeriod> findFirstByDate(@Param("date") LocalDate date, @Param("user") User user);

}
