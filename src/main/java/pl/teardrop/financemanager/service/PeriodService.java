package pl.teardrop.financemanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Period;
import pl.teardrop.financemanager.repository.PeriodRepository;
import pl.teardrop.financemanager.service.exceptions.PeriodExistsException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {

	private final PeriodRepository periodRepository;

	public List<Period> getByUser(User user) {
		return periodRepository.findByUserOrderByStartsOn(user);
	}

	public Optional<Period> getById(long id) {
		return periodRepository.findById(id);
	}

	public Period save(Period period) {
		validatePeriod(period);
		Period periodAdded = periodRepository.save(period);
		log.info("Saved period id={}, userId={}", periodAdded.getId(), periodAdded.getUser().getId());
		return periodAdded;
	}

	public void delete(long id) {
		periodRepository.deleteById(id);
		log.info("Period deleted id={}", id);
	}

	public Period getByDate(LocalDate date, User user) {
		return periodRepository.findFirstByDate(date, user)
				.orElseGet(() -> {
					log.info("Period for date " + date + " not found");
					Period period = new Period();
					period.setUser(user);
					period.setStartsOn(getDefaultStartsOn(date));
					period.setEndsOn(getDefaultEndsOn(date));
					save(period);
					return period;
				});
	}

	private void validatePeriod(Period period) throws PeriodExistsException {
		if (periodRepository.findFirstByDate(period.getStartsOn(), period.getUser()).isPresent()
			|| periodRepository.findFirstByDate(period.getEndsOn(), period.getUser()).isPresent()) {
			throw new PeriodExistsException("Period overlaps with another period");
		}
	}

	private static LocalDate getDefaultStartsOn(LocalDate date) {
		return date.withDayOfMonth(1);
	}

	private static LocalDate getDefaultEndsOn(LocalDate date) {
		return date.plusMonths(1).minusDays(1);
	}
}
