package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.SummaryDTO;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SummaryCalculator {

	private final FinancialRecordService financialRecordService;
	private final CategoryService categoryService;

	public List<SummaryDTO> getSummary(AccountingPeriodId periodId, FinancialRecordType type) {
		return financialRecordService.getByPeriodIdAndType(periodId, type).stream()
				.collect(Collectors.groupingBy(FinancialRecord::getCategoryId,
											   Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)))
				.entrySet().stream()
				.map(mapEntry -> {
					CategoryId categoryId = mapEntry.getKey();
					BigDecimal amount = mapEntry.getValue();
					Category category = categoryService.getById(categoryId).orElseThrow(() -> new RuntimeException("Category not found for id=" + categoryId.getId()));
					return new SummaryDTO(amount, category.getName());
				})
				.toList();
	}

}
