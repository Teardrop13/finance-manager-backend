package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;

@Service
@AllArgsConstructor
public class FinancialRecordFactory {

	private final CategoryService categoryService;
	private final AccountingPeriodService accountingPeriodService;

	public FinancialRecord getFinancialRecord(CreateFinancialRecordCommand command) throws CategoryNotFoundException {
		Category category = categoryService.getByUserAndTypeAndName(command.getUserId(), command.getType(), command.getCategory())
				.orElseThrow(() -> new CategoryNotFoundException("Failed to create FinancialRecord. Category " + command.getCategory() + " not found for userId=" + command.getUserId().getId()));

		AccountingPeriod period = accountingPeriodService.getByDate(command.getTransactionDate(), command.getUserId());

		return FinancialRecord.builder()
				.userId(command.getUserId())
				.description(command.getDescription())
				.amount(command.getAmount())
				.transactionDate(command.getTransactionDate())
				.type(command.getType())
				.categoryId(category.categoryId())
				.accountingPeriodId(period.accountingPeriodId())
				.build();
	}

}
