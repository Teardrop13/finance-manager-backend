package io.github.teardrop13.financemanager.repository;

import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.financemanager.model.Period;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface PeriodRepository extends Repository<Period, Long> {

	Optional<Period> findById(Long id);

	List<Period> findAll();

	List<Period> findByUserOrderByStartsOn(User user);

	Period save(Period period);

	void deleteById(Long id);

}
