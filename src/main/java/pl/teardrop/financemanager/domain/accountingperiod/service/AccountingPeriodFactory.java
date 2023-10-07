package pl.teardrop.financemanager.domain.accountingperiod.service;

import org.springframework.security.access.prepost.PreAuthorize;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class AccountingPeriodFactory {

	@PreAuthorize("#userId.getId() == authentication.principal.getId()")
	public AccountingPeriod getAccountingPeriod(UserId userId, LocalDate date) {
		return AccountingPeriod.builder()
				.userId(userId)
				.startsOn(getDefaultStartsOn(date))
				.endsOn(getDefaultEndsOn(date))
				.build();
	}

	private static LocalDate getDefaultStartsOn(LocalDate date) {
		return date.withDayOfMonth(1);
	}

	private static LocalDate getDefaultEndsOn(LocalDate date) {
		return date.with(TemporalAdjusters.lastDayOfMonth());
	}

}
