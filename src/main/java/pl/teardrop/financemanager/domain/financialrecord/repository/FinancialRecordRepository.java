package pl.teardrop.financemanager.domain.financialrecord.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@Component
public interface FinancialRecordRepository extends Repository<FinancialRecord, Long> {

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().getUserId().getId() == authentication.principal.id : true")
	Optional<FinancialRecord> findById(long id);

	@PostFilter("filterObject.getUserId().getId() == authentication.principal.id")
	List<FinancialRecord> findByAccountingPeriodIdAndType(AccountingPeriodId accountingPeriodId, FinancialRecordType type);

	@PreAuthorize("#userId.getId() == authentication.principal.id")
	List<FinancialRecord> findByUserIdAndAccountingPeriodIdAndType(UserId userId,
																   AccountingPeriodId accountingPeriodId,
																   FinancialRecordType type,
																   Pageable pageable);

	@PreAuthorize("#financialRecord.getUserId().getId() == authentication.principal.id")
	FinancialRecord save(FinancialRecord financialRecord);

	@PreAuthorize("#financialRecord.getUserId().getId() == authentication.principal.id")
	void delete(FinancialRecord financialRecord);

	@PreAuthorize("#userId.getId() == authentication.principal.id")
	int countByUserIdAndAccountingPeriodIdAndType(UserId userId, AccountingPeriodId accountingPeriodId, FinancialRecordType type);
}
