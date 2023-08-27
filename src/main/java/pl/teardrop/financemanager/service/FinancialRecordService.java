package pl.teardrop.financemanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.dto.UpdateFinancialRecordCommand;
import pl.teardrop.financemanager.model.AccountingPeriod;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.repository.FinancialRecordRepository;
import pl.teardrop.financemanager.service.exceptions.CategoryNotFoundException;
import pl.teardrop.financemanager.service.exceptions.FinancialRecordNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialRecordService {

	private final FinancialRecordRepository recordRepository;
	private final AccountingPeriodService accountingPeriodService;
	private final FinancialRecordFactory financialRecordFactory;

	public List<FinancialRecord> getByUser(User user,
										   int periodId,
										   FinancialRecordType type,
										   int page,
										   int pageSize,
										   String sortBy,
										   boolean isAscending) {
		Sort sort = Sort.by(sortBy);
		sort = isAscending ? sort.ascending() : sort.descending();

		return recordRepository.findByUserAndAccountingPeriodIdAndType(user, periodId, type, PageRequest.of(page, pageSize, sort));
	}

	public List<FinancialRecord> getByUser(User user, int page, int pageSize) {
		return recordRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, pageSize));
	}

	public Optional<FinancialRecord> getById(long id) {
		return recordRepository.findById(id);
	}

	public List<FinancialRecord> getByCategory(Category category) {
		return recordRepository.findByCategory(category);
	}

	public List<FinancialRecord> getByPeriodIdAndType(long periodId, FinancialRecordType type) {
		return recordRepository.findByPeriodIdAndType(periodId, type);
	}

	public int getRecordsCount(User user) {
		return recordRepository.countByUser(user);
	}

	public int getRecordsCount(User user, int periodId, FinancialRecordType type) {
		return recordRepository.countByUserAndAccountingPeriodIdAndType(user, periodId, type);
	}

	public FinancialRecord create(User user, CreateFinancialRecordCommand createCommand) throws CategoryNotFoundException {
		FinancialRecord financialRecord = financialRecordFactory.getFinancialRecord(user, createCommand);
		return save(financialRecord);
	}

	public FinancialRecord update(UpdateFinancialRecordCommand updateCommand) throws FinancialRecordNotFoundException {
		FinancialRecord financialRecord = getById(updateCommand.getRecordId())
				.orElseThrow(() -> new FinancialRecordNotFoundException("Record with id=%d not found".formatted(updateCommand.getRecordId())));

		financialRecord.setDescription(updateCommand.getDescription());
		financialRecord.setAmount(updateCommand.getAmount());
		return save(financialRecord);
	}

	public FinancialRecord save(FinancialRecord financialRecord) {
		if (financialRecord.getId() == null) {
			financialRecord.setCreatedAt(LocalDateTime.now());
		}
		AccountingPeriod period = accountingPeriodService.getByDate(financialRecord.getTransactionDate(), financialRecord.getUser());
		financialRecord.setAccountingPeriod(period);
		FinancialRecord financialRecordSaved = recordRepository.save(financialRecord);
		log.info("Saved record id={}, userId={}", financialRecordSaved.getId(), financialRecordSaved.getUser().getId());
		return financialRecordSaved;
	}

	public void delete(long id) throws FinancialRecordNotFoundException {
		FinancialRecord financialRecord = getById(id)
				.orElseThrow(() -> new FinancialRecordNotFoundException("FinancialRecord with id=%d not found.".formatted(id)));

		delete(financialRecord);
	}

	public void delete(FinancialRecord financialRecord) {
		recordRepository.delete(financialRecord);
		log.info("Deleted record id={}, userId={}", financialRecord.getId(), financialRecord.getUser().getId());
	}
}
