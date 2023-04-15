package io.github.teardrop13.financemanager.services;

import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.financemanager.domain.category.Category;
import io.github.teardrop13.financemanager.domain.record.FinancialRecord;
import io.github.teardrop13.financemanager.repositories.RecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RecordService {

	@Autowired
	private RecordRepository recordRepository;

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
		return recordRepository.getByCategory(category);
	}

	public FinancialRecord save(FinancialRecord financialRecord) {
		FinancialRecord financialRecordAdded = recordRepository.save(financialRecord);
		log.info("Saved record id={}, userId={}", financialRecordAdded.getId(), financialRecordAdded.getUser().getId());
		return financialRecordAdded;
	}

	public void delete(FinancialRecord financialRecord) {
		recordRepository.deleteById(financialRecord.getId());
		log.info("Deleted record id={}, userId={}", financialRecord.getId(), financialRecord.getUser().getId());
	}
}
