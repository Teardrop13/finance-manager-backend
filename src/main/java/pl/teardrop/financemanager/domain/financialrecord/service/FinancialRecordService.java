package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
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

	@PreAuthorize("#userId.getId() == authentication.principal.getId() "
				  + "&& @accountingPeriodAccessTest.test(#accountingPeriodId, authentication)")
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

	@PreAuthorize("@financialRecordAccessTest.test(#financialRecordId, authentication)")
	public Optional<FinancialRecord> getById(FinancialRecordId financialRecordId) {
		return recordRepository.findById(financialRecordId.getId());
	}

	@PreAuthorize("@accountingPeriodAccessTest.test(#accountingPeriodId, authentication)")
	public List<FinancialRecord> getByPeriodIdAndType(AccountingPeriodId accountingPeriodId, FinancialRecordType type) {
		return recordRepository.findByAccountingPeriodIdAndType(accountingPeriodId, type);
	}

	@PreAuthorize("@accountingPeriodAccessTest.test(#accountingPeriodId, authentication)")
	public List<FinancialRecord> getByPeriodId(AccountingPeriodId accountingPeriodId) {
		return recordRepository.findByAccountingPeriodId(accountingPeriodId);
	}

	@PreAuthorize("#userId.getId() == authentication.principal.getId() "
				  + "&& @accountingPeriodAccessTest.test(#accountingPeriodId, authentication)")
	public int getRecordsCount(UserId userId, AccountingPeriodId accountingPeriodId, FinancialRecordType type) {
		return recordRepository.countByUserIdAndAccountingPeriodIdAndType(userId, accountingPeriodId, type);
	}

	@PreAuthorize("#createCommand.userId().getId() == authentication.principal.getId()")
	public FinancialRecord create(CreateFinancialRecordCommand createCommand) throws CategoryNotFoundException {
		FinancialRecord financialRecord = financialRecordFactory.getFinancialRecord(createCommand);
		return save(financialRecord);
	}

	@PreAuthorize("@financialRecordAccessTest.test(#updateCommand.recordId(), authentication)")
	public FinancialRecord update(UpdateFinancialRecordCommand updateCommand) throws FinancialRecordNotFoundException, CategoryNotFoundException {
		FinancialRecord financialRecord = getById(updateCommand.recordId())
				.orElseThrow(() -> new FinancialRecordNotFoundException("Record with id=%d not found".formatted(updateCommand.recordId().getId())));

		Category category = categoryService.getByUserAndTypeAndName(financialRecord.getUserId(), financialRecord.getType(), updateCommand.category())
				.orElseThrow(() -> new CategoryNotFoundException("Failed to create FinancialRecord. Category " + updateCommand.category() + " not found for userId=" + financialRecord.getUserId().getId()));

		financialRecord.setDescription(updateCommand.description());
		financialRecord.setTransactionDate(updateCommand.transactionDate());
		financialRecord.setCategoryId(category.categoryId());
		financialRecord.setAmount(updateCommand.amount());
		return save(financialRecord);
	}

	@PreAuthorize("(#financialRecord.isNew() || @financialRecordAccessTest.test(#financialRecord.financialRecordId(), authentication)) "
				  + "&& #financialRecord.getUserId().getId() == authentication.principal.getId()")
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

	@PreAuthorize("@financialRecordAccessTest.test(#financialRecordId, authentication)")
	public void delete(FinancialRecordId financialRecordId) throws FinancialRecordNotFoundException {
		FinancialRecord financialRecord = getById(financialRecordId)
				.orElseThrow(() -> new FinancialRecordNotFoundException("FinancialRecord with id=%d not found.".formatted(financialRecordId.getId())));

		recordRepository.delete(financialRecord);
		log.info("Deleted record id={}, userId={}", financialRecord.getId(), financialRecord.getUserId().getId());
	}
}
