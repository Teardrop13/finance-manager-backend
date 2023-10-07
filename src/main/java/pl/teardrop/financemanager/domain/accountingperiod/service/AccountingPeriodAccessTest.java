package pl.teardrop.financemanager.domain.accountingperiod.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.domain.User;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.repository.AccountingPeriodRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class AccountingPeriodAccessTest {

	private final AccountingPeriodRepository accountingPeriodRepository;

	public boolean test(@NonNull AccountingPeriodId accountingPeriodId, Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		UserId userId = accountingPeriodRepository.findById(accountingPeriodId.getId())
				.map(AccountingPeriod::getUserId)
				.orElse(null);

		boolean result = Objects.equals(user.userId(), userId);

		log.info("Testing if accountingPeriodId={} belongs to userid={}, result={}", accountingPeriodId.getId(), user.getId(), result);

		return result;
	}

}
