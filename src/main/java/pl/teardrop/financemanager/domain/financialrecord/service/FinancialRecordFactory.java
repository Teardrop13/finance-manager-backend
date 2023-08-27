package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;

@Service
@AllArgsConstructor
public class FinancialRecordFactory {

	private final CategoryService categoryService;
	private final AccountingPeriodService accountingPeriodService;

	public FinancialRecord getFinancialRecord(User user, CreateFinancialRecordCommand command) throws CategoryNotFoundException {
		Category category = categoryService.getByUserAndTypeAndName(user, command.getType(), command.getCategory())
				.orElseThrow(() -> new CategoryNotFoundException("Failed to create FinancialRecord. Category " + command.getCategory() + " not found for user id=" + user.getId()));

		AccountingPeriod period = accountingPeriodService.getByDate(command.getTransactionDate(), user);

		FinancialRecord financialRecord = new FinancialRecord();
		financialRecord.setUser(user);
		financialRecord.setDescription(command.getDescription());
		financialRecord.setAmount(command.getAmount());
		financialRecord.setTransactionDate(command.getTransactionDate());
		financialRecord.setType(command.getType());
		financialRecord.setCategory(category);
		financialRecord.setAccountingPeriod(period);
		return financialRecord;
	}

}
