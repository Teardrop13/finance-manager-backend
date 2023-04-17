package pl.teardrop.financemanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.AccountingPeriod;
import pl.teardrop.financemanager.repository.AccountingPeriodRepository;
import pl.teardrop.financemanager.service.exceptions.PeriodExistsException;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {

	private final AccountingPeriodRepository accountingPeriodRepository;

	public List<AccountingPeriod> getByUser(User user) {
		return accountingPeriodRepository.findByUserOrderByStartsOn(user);
	}

	public Optional<AccountingPeriod> getById(long id) {
		return accountingPeriodRepository.findById(id);
	}

	public AccountingPeriod save(AccountingPeriod period) {
		validatePeriod(period);
		AccountingPeriod periodAdded = accountingPeriodRepository.save(period);
		log.info("Saved period id={}, userId={}", periodAdded.getId(), periodAdded.getUser().getId());
		return periodAdded;
	}

	public void delete(long id) {
		accountingPeriodRepository.deleteById(id);
		log.info("Period deleted id={}", id);
	}

	public AccountingPeriod getByDate(LocalDate date, User user) {
		return accountingPeriodRepository.findFirstByDate(date, user)
				.orElseGet(() -> {
					log.info("Period for date " + date + " not found");
					AccountingPeriod period = new AccountingPeriod();
					period.setUser(user);
					period.setStartsOn(getDefaultStartsOn(date));
					period.setEndsOn(getDefaultEndsOn(date));
					save(period);
					return period;
				});
	}

	private void validatePeriod(AccountingPeriod period) throws PeriodExistsException {
		if (accountingPeriodRepository.findFirstByDate(period.getStartsOn(), period.getUser()).isPresent()
			|| accountingPeriodRepository.findFirstByDate(period.getEndsOn(), period.getUser()).isPresent()) {
			throw new PeriodExistsException("Period overlaps with another period");
		}
	}

	private static LocalDate getDefaultStartsOn(LocalDate date) {
		return date.withDayOfMonth(1);
	}

	private static LocalDate getDefaultEndsOn(LocalDate date) {
		return date.with(TemporalAdjusters.lastDayOfMonth());
	}
}
