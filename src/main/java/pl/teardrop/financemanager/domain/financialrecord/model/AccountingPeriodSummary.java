package pl.teardrop.financemanager.domain.financialrecord.model;

import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.financialrecord.dto.AccountingPeriodSummaryDTO;

import java.math.BigDecimal;

public record AccountingPeriodSummary(BigDecimal income,
									  BigDecimal expense,
									  AccountingPeriod accountingPeriod) {

	public static AccountingPeriodSummary zero(AccountingPeriod accountingPeriod) {
		return new AccountingPeriodSummary(BigDecimal.ZERO, BigDecimal.ZERO, accountingPeriod);
	}

	public AccountingPeriodSummary(BigDecimal amount, FinancialRecordType type, AccountingPeriod accountingPeriod) {
		this(
				type == FinancialRecordType.INCOME ? amount : BigDecimal.ZERO,
				type == FinancialRecordType.EXPENSE ? amount : BigDecimal.ZERO,
				accountingPeriod
		);
	}

	public AccountingPeriodSummary add(AccountingPeriodSummary other) {
		return new AccountingPeriodSummary(income.add(other.income),
										   expense.add(other.expense),
										   other.accountingPeriod());
	}

	public AccountingPeriodSummaryDTO toDto() {
		return new AccountingPeriodSummaryDTO(income, expense, accountingPeriod.getStartsOn(), accountingPeriod.getEndsOn(), accountingPeriod.getId());
	}
}
