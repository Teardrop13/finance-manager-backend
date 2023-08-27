package pl.teardrop.financemanager.domain.accountingperiod.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.repository.AccountingPeriodRepository;
import pl.teardrop.financemanager.domain.accountingperiod.exception.PeriodExistsException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountingPeriodService {

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

	private void validatePeriod(AccountingPeriod period) throws PeriodExistsException {
		if (accountingPeriodRepository.findFirstByDate(period.getStartsOn(), period.getUser()).isPresent()
			|| accountingPeriodRepository.findFirstByDate(period.getEndsOn(), period.getUser()).isPresent()) {
			throw new PeriodExistsException("Period overlaps with another period");
		}
	}

	public AccountingPeriod getCurrent(User user) {
		return getByDate(LocalDate.now(), user);
	}

	@PostAuthorize("returnObject.getUser().getId() == authentication.principal.id")
	public AccountingPeriod getNext(long currentId, User user) {
		AccountingPeriod accountingPeriod = getById(currentId).orElseThrow(() -> new RuntimeException("AccountingPeriod for id=" + currentId + " and user id=" + user.getId() + " not found"));
		return getByDate(accountingPeriod.getEndsOn().plusDays(1), user);
	}

	@PostAuthorize("returnObject.getUser().getId() == authentication.principal.id")
	public AccountingPeriod getPrevious(long currentId, User user) {
		AccountingPeriod accountingPeriod = getById(currentId).orElseThrow(() -> new RuntimeException("AccountingPeriod for id=" + currentId + " and user id=" + user.getId() + " not found"));
		return getByDate(accountingPeriod.getStartsOn().minusDays(1), user);
	}

	public AccountingPeriod getByDate(LocalDate date, User user) {
		return accountingPeriodRepository.findFirstByDate(date, user)
				.orElseGet(() -> {
					log.info("Period for date {} not found", date);
					AccountingPeriod period = new AccountingPeriodFactory().getAccountingPeriod(user, date);
					save(period);
					return period;
				});
	}
}
