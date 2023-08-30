package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.domain.financialrecord.dto.UpdateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.exception.FinancialRecordNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;
import pl.teardrop.financemanager.domain.financialrecord.repository.FinancialRecordRepository;

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
	private final CategoryService categoryService;

	public List<FinancialRecord> getPage(UserId userId,
										 AccountingPeriodId accountingPeriodId,
										 FinancialRecordType type,
										 int page,
										 int pageSize,
										 String sortBy,
										 boolean isAscending) {
		Sort sort = Sort.by(sortBy);
		sort = isAscending ? sort.ascending() : sort.descending();

		return recordRepository.findByUserIdAndAccountingPeriodIdAndType(userId, accountingPeriodId, type, PageRequest.of(page, pageSize, sort));
	}

	public Optional<FinancialRecord> getById(FinancialRecordId financialRecordId) {
		return recordRepository.findById(financialRecordId.getId());
	}

	public List<FinancialRecord> getByPeriodIdAndType(AccountingPeriodId periodId, FinancialRecordType type) {
		return recordRepository.findByAccountingPeriodIdAndType(periodId, type);
	}

	public List<FinancialRecord> getByPeriodId(AccountingPeriodId periodId) {
		return recordRepository.findByAccountingPeriodId(periodId);
	}

	public int getRecordsCount(UserId userId, AccountingPeriodId periodId, FinancialRecordType type) {
		return recordRepository.countByUserIdAndAccountingPeriodIdAndType(userId, periodId, type);
	}

	public FinancialRecord create(CreateFinancialRecordCommand createCommand) throws CategoryNotFoundException {
		FinancialRecord financialRecord = financialRecordFactory.getFinancialRecord(createCommand);
		return save(financialRecord);
	}

	public FinancialRecord update(UpdateFinancialRecordCommand updateCommand) throws FinancialRecordNotFoundException {
		FinancialRecord financialRecord = getById(updateCommand.getRecordId())
				.orElseThrow(() -> new FinancialRecordNotFoundException("Record with id=%d not found".formatted(updateCommand.getRecordId().getId())));

		financialRecord.setDescription(updateCommand.getDescription());
		financialRecord.setAmount(updateCommand.getAmount());
		return save(financialRecord);
	}

	public FinancialRecord save(FinancialRecord financialRecord) {
		if (financialRecord.getId() == null) {
			financialRecord.setCreatedAt(LocalDateTime.now());
		}
		AccountingPeriod period = accountingPeriodService.getByDate(financialRecord.getTransactionDate(), financialRecord.getUserId());
		financialRecord.setAccountingPeriodId(period.accountingPeriodId());
		FinancialRecord financialRecordSaved = recordRepository.save(financialRecord);
		log.info("Saved record id={}, userId={}", financialRecordSaved.getId(), financialRecordSaved.getUserId().getId());
		return financialRecordSaved;
	}

	public void delete(FinancialRecordId financialRecordId) throws FinancialRecordNotFoundException {
		FinancialRecord financialRecord = getById(financialRecordId)
				.orElseThrow(() -> new FinancialRecordNotFoundException("FinancialRecord with id=%d not found.".formatted(financialRecordId.getId())));

		delete(financialRecord);
	}

	public void delete(FinancialRecord financialRecord) {
		recordRepository.delete(financialRecord);
		log.info("Deleted record id={}, userId={}", financialRecord.getId(), financialRecord.getUserId().getId());
	}

	public FinancialRecordDTO getDto(FinancialRecord financialRecord) {
		Category category = categoryService.getById(financialRecord.getCategoryId()).orElseThrow(() -> new RuntimeException("Cannot create FinancialRecordDto. Category not found for categoryId=" + financialRecord.getCategoryId().getId()));
		return new FinancialRecordDTO(financialRecord.getId(),
									  financialRecord.getDescription(),
									  financialRecord.getAmount(),
									  category.getName(),
									  financialRecord.getType(),
									  financialRecord.getTransactionDate());
	}
}
