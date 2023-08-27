package pl.teardrop.financemanager.domain.accountingperiod.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.exception.PeriodExistsException;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.repository.AccountingPeriodRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountingPeriodService {

	private final AccountingPeriodRepository accountingPeriodRepository;

	public List<AccountingPeriod> getByUser(UserId userId) {
		return accountingPeriodRepository.findByUserIdOrderByStartsOn(userId);
	}

	public Optional<AccountingPeriod> getById(AccountingPeriodId accountingPeriodId) {
		return accountingPeriodRepository.findById(accountingPeriodId.getId());
	}

	public AccountingPeriod save(AccountingPeriod period) {
		validatePeriod(period);
		AccountingPeriod periodAdded = accountingPeriodRepository.save(period);
		log.info("Saved period id={}, userId={}", periodAdded.getId(), periodAdded.getUserId().getId());
		return periodAdded;
	}

	private void validatePeriod(AccountingPeriod period) throws PeriodExistsException {
		if (accountingPeriodRepository.findFirstByDate(period.getStartsOn(), period.getUserId()).isPresent()
			|| accountingPeriodRepository.findFirstByDate(period.getEndsOn(), period.getUserId()).isPresent()) {
			throw new PeriodExistsException("Period overlaps with another period");
		}
	}

	public AccountingPeriod getCurrent(UserId userId) {
		return getByDate(LocalDate.now(), userId);
	}

	@PostAuthorize("returnObject.getUserId().getId() == authentication.principal.id")
	public AccountingPeriod getNext(AccountingPeriodId currentId, UserId userId) {
		AccountingPeriod accountingPeriod = getById(currentId).orElseThrow(() -> new RuntimeException("AccountingPeriod for id=" + currentId + " and userId=" + userId.getId() + " not found"));
		return getByDate(accountingPeriod.getEndsOn().plusDays(1), userId);
	}

	@PostAuthorize("returnObject.getUserId().getId() == authentication.principal.id")
	public AccountingPeriod getPrevious(AccountingPeriodId currentId, UserId userId) {
		AccountingPeriod accountingPeriod = getById(currentId).orElseThrow(() -> new RuntimeException("AccountingPeriod for id=" + currentId + " and userId=" + userId.getId() + " not found"));
		return getByDate(accountingPeriod.getStartsOn().minusDays(1), userId);
	}

	public AccountingPeriod getByDate(LocalDate date, UserId userId) {
		return accountingPeriodRepository.findFirstByDate(date, userId)
				.orElseGet(() -> {
					log.info("Period for date {} not found", date);
					AccountingPeriod period = new AccountingPeriodFactory().getAccountingPeriod(userId, date);
					save(period);
					return period;
				});
	}
}
