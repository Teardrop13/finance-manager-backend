package pl.teardrop.financemanager.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Period;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public interface PeriodRepository extends Repository<Period, Long> {

	Optional<Period> findById(Long id);

	List<Period> findAll();

	List<Period> findByUserOrderByStartsOn(User user);

	Period save(Period period);

	void deleteById(Long id);

	@Query(value = "SELECT p "
				   + "FROM Period p "
				   + "WHERE p.user = :user "
				   + "AND p.startsOn <= :date "
				   + "AND p.endsOn >= :date")
	Optional<Period> findFirstByDate(@Param("date") LocalDate date, @Param("user") User user);

}
