package pl.teardrop.financemanager.domain.accountingperiod.service;

import org.springframework.security.access.prepost.PreAuthorize;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class AccountingPeriodFactory {

	@PreAuthorize("#userId.getId() == authentication.principal.getId()")
	public AccountingPeriod getAccountingPeriod(UserId userId, LocalDate date) {
		AccountingPeriod period = new AccountingPeriod();
		period.setUserId(userId);
		period.setStartsOn(getDefaultStartsOn(date));
		period.setEndsOn(getDefaultEndsOn(date));
		return period;
	}

	private static LocalDate getDefaultStartsOn(LocalDate date) {
		return date.withDayOfMonth(1);
	}

	private static LocalDate getDefaultEndsOn(LocalDate date) {
		return date.with(TemporalAdjusters.lastDayOfMonth());
	}

}
