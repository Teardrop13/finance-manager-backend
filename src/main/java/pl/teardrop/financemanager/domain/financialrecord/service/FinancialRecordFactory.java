package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
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

	public FinancialRecord getFinancialRecord(UserId userId, CreateFinancialRecordCommand command) throws CategoryNotFoundException {
		Category category = categoryService.getByUserAndTypeAndName(userId, command.getType(), command.getCategory())
				.orElseThrow(() -> new CategoryNotFoundException("Failed to create FinancialRecord. Category " + command.getCategory() + " not found for userId=" + userId.getId()));

		AccountingPeriod period = accountingPeriodService.getByDate(command.getTransactionDate(), userId);

		FinancialRecord financialRecord = new FinancialRecord();
		financialRecord.setUserId(userId);
		financialRecord.setDescription(command.getDescription());
		financialRecord.setAmount(command.getAmount());
		financialRecord.setTransactionDate(command.getTransactionDate());
		financialRecord.setType(command.getType());
		financialRecord.setCategoryId(category.categoryId());
		financialRecord.setAccountingPeriodId(period.accountingPeriodId());
		return financialRecord;
	}

}
