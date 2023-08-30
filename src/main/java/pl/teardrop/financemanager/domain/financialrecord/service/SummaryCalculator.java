package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.model.CategorySummary;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;
import pl.teardrop.financemanager.domain.financialrecord.model.RecordTypeSummary;

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

	public List<CategorySummary> getSummaryByCategory(UserId userId, AccountingPeriodId periodId, FinancialRecordType type) {
		Map<CategoryId, Category> categoriesById = categoryService.getByUserAndType(userId, type).stream().collect(Collectors.toMap(Category::categoryId, Function.identity()));

		return new ArrayList<>(financialRecordService.getByPeriodIdAndType(periodId, type).stream()
									   .map(r -> new CategorySummary(r.getAmount(), categoriesById.get(r.getCategoryId())))
									   .collect(Collectors.groupingBy(CategorySummary::category,
																	  Collectors.reducing(CategorySummary.ZERO, CategorySummary::add)))
									   .values());
	}

	public List<RecordTypeSummary> getSummaryByRecordType(AccountingPeriodId periodId) {
		return new ArrayList<>(financialRecordService.getByPeriodId(periodId).stream()
									   .map(r -> new RecordTypeSummary(r.getAmount(), r.getType()))
									   .collect(Collectors.groupingBy(RecordTypeSummary::type,
																	  Collectors.reducing(RecordTypeSummary.ZERO, RecordTypeSummary::add)))
									   .values());
	}
}
