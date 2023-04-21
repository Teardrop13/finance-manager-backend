package pl.teardrop.financemanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.model.AccountingPeriod;
import pl.teardrop.financemanager.repository.FinancialRecordRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialRecordService {

	private final FinancialRecordRepository recordRepository;
	private final AccountingPeriodService accountingPeriodService;

	public List<FinancialRecord> getByUser(User user) {
		return recordRepository.findByUserOrderByCreatedAtDesc(user);
	}

	public List<FinancialRecord> get(Category category) {
		return recordRepository.findByCategory(category);
	}

	public Optional<FinancialRecord> getById(long id) {
		return recordRepository.findById(id);
	}

	public Collection<FinancialRecord> getByCategory(Category category) {
		return recordRepository.findByCategory(category);
	}

	public FinancialRecord save(FinancialRecord financialRecord) {
		AccountingPeriod period = accountingPeriodService.getByDate(financialRecord.getTransactionDate(), financialRecord.getUser());
		financialRecord.setAccountingPeriod(period);
		FinancialRecord financialRecordAdded = recordRepository.save(financialRecord);
		log.info("Saved record id={}, userId={}", financialRecordAdded.getId(), financialRecordAdded.getUser().getId());
		return financialRecordAdded;
	}

	public void delete(FinancialRecord financialRecord) {
		recordRepository.delete(financialRecord);
		log.info("Deleted record id={}, userId={}", financialRecord.getId(), financialRecord.getUser().getId());
	}
}
