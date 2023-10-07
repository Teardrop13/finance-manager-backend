package pl.teardrop.financemanager.domain.accountingperiod.service;

import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class AccountingPeriodFactory {

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
