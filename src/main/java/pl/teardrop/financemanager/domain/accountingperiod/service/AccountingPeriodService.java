package pl.teardrop.financemanager.domain.accountingperiod.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.domain.UserId;
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
	private final AccountingPeriodValidator accountingPeriodValidator;

	@PreAuthorize("#userId.getId() == authentication.principal.getId()")
	public List<AccountingPeriod> getByUser(UserId userId) {
		return accountingPeriodRepository.findByUserIdOrderByStartsOn(userId);
	}

	@PreAuthorize("@accountingPeriodAccessTest.test(#accountingPeriodId, authentication)")
	public Optional<AccountingPeriod> getById(AccountingPeriodId accountingPeriodId) {
		return accountingPeriodRepository.findById(accountingPeriodId.getId());
	}

	@PreAuthorize("(#accountingPeriod.isNew() || @accountingPeriodAccessTest.test(#accountingPeriod.accountingPeriodId(), authentication)) "
				  + "&& #userId.getId() == authentication.principal.getId()")
	public AccountingPeriod save(AccountingPeriod accountingPeriod) {
		accountingPeriodValidator.validate(accountingPeriod);
		AccountingPeriod periodAdded = accountingPeriodRepository.save(accountingPeriod);
		log.info("Saved accountingPeriod id={}, userId={}", periodAdded.getId(), periodAdded.getUserId().getId());
		return periodAdded;
	}

	@PreAuthorize("#userId.getId() == authentication.principal.id")
	public AccountingPeriod getCurrent(UserId userId) {
		return getByDate(LocalDate.now(), userId);
	}

	@PreAuthorize("@accountingPeriodAccessTest.test(#currentId, authentication) && "
				  + "#userId.getId() == authentication.principal.id")
	public AccountingPeriod getNext(AccountingPeriodId currentId, UserId userId) {
		AccountingPeriod accountingPeriod = getById(currentId).orElseThrow(() -> new RuntimeException("AccountingPeriod for id=" + currentId + " and userId=" + userId.getId() + " not found"));
		return getByDate(accountingPeriod.getEndsOn().plusDays(1), userId);
	}

	@PreAuthorize("@accountingPeriodAccessTest.test(#currentId, authentication) && "
				  + "#userId.getId() == authentication.principal.id")
	public AccountingPeriod getPrevious(AccountingPeriodId currentId, UserId userId) {
		AccountingPeriod accountingPeriod = getById(currentId).orElseThrow(() -> new RuntimeException("AccountingPeriod for id=" + currentId + " and userId=" + userId.getId() + " not found"));
		return getByDate(accountingPeriod.getStartsOn().minusDays(1), userId);
	}

	@PreAuthorize("#userId.getId() == authentication.principal.id")
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
