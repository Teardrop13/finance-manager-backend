package pl.teardrop.financemanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.SummaryDTO;
import pl.teardrop.financemanager.model.AccountingPeriod;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.repository.FinancialRecordRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialRecordService {

	private final FinancialRecordRepository recordRepository;
	private final AccountingPeriodService accountingPeriodService;

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

	public int getRecordsCount(User user) {
		return recordRepository.countByUser(user);
	}

	public int getRecordsCount(User user, int periodId, FinancialRecordType type) {
		return recordRepository.countByUserAndAccountingPeriodIdAndType(user, periodId, type);
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
		financialRecord.setCreatedAt(LocalDateTime.now());
		FinancialRecord financialRecordAdded = recordRepository.save(financialRecord);
		log.info("Saved record id={}, userId={}", financialRecordAdded.getId(), financialRecordAdded.getUser().getId());
		return financialRecordAdded;
	}

	public void delete(FinancialRecord financialRecord) {
		recordRepository.delete(financialRecord);
		log.info("Deleted record id={}, userId={}", financialRecord.getId(), financialRecord.getUser().getId());
	}

	public List<SummaryDTO> getSummary(int periodId, FinancialRecordType type) {
		return new ArrayList<>(recordRepository.findByPeriodIdAndType(periodId, type).stream()
									   .map(r -> new SummaryDTO(r.getAmount(), r.getCategory().getName()))
									   .collect(Collectors.groupingBy(SummaryDTO::category,
																	  Collectors.reducing(SummaryDTO.ZERO, SummaryDTO::add)))
									   .values());
	}
}
