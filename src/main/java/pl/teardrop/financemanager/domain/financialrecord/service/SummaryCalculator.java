package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.exception.AccountingPeriodNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.model.AccountingPeriodSummary;
import pl.teardrop.financemanager.domain.financialrecord.model.CategorySummary;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SummaryCalculator {

	private final FinancialRecordService financialRecordService;
	private final CategoryService categoryService;
	private final AccountingPeriodService accountingPeriodService;

	public List<CategorySummary> getSummaryByCategory(UserId userId, AccountingPeriodId periodId, FinancialRecordType type) {
		Map<CategoryId, Category> categoriesById = categoryService.getByUserAndType(userId, type).stream().collect(Collectors.toMap(Category::categoryId, Function.identity()));

		return new ArrayList<>(financialRecordService.getByPeriodIdAndType(periodId, type).stream()
									   .map(r -> new CategorySummary(r.getAmount(), categoriesById.get(r.getCategoryId())))
									   .collect(Collectors.groupingBy(CategorySummary::category,
																	  Collectors.reducing(CategorySummary.ZERO, CategorySummary::add)))
									   .values());
	}

	public AccountingPeriodSummary getAccountingPeriodSummary(AccountingPeriodId periodId) throws AccountingPeriodNotFoundException {
		AccountingPeriod accountingPeriod = accountingPeriodService.getById(periodId).orElseThrow(() -> new AccountingPeriodNotFoundException("Period id=%d does not exist".formatted(periodId.getId())));

		return financialRecordService.getByPeriodId(periodId).stream()
				.map(r -> new AccountingPeriodSummary(r.getAmount(), r.getType(), accountingPeriod))
				.reduce(AccountingPeriodSummary.zero(accountingPeriod), AccountingPeriodSummary::add);
	}
}
