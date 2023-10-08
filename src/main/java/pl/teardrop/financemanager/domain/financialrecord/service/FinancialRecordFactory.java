package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.service.CategoryRetrievingService;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;

@Service
@AllArgsConstructor
public class FinancialRecordFactory {

	private final CategoryRetrievingService categoryRetrievingService;
	private final AccountingPeriodService accountingPeriodService;

	public FinancialRecord getFinancialRecord(CreateFinancialRecordCommand command) throws CategoryNotFoundException {
		Category category = categoryRetrievingService.getByUserAndTypeAndName(command.userId(), command.type(), command.category())
				.orElseThrow(() -> new CategoryNotFoundException("Failed to create FinancialRecord. Category " + command.category() + " not found for userId=" + command.userId().getId()));

		AccountingPeriod period = accountingPeriodService.getByDate(command.transactionDate(), command.userId());

		return FinancialRecord.builder()
				.userId(command.userId())
				.description(command.description())
				.amount(command.amount())
				.transactionDate(command.transactionDate())
				.type(command.type())
				.categoryId(category.categoryId())
				.accountingPeriodId(period.accountingPeriodId())
				.build();
	}
}
