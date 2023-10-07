package pl.teardrop.financemanager.domain.financialrecord.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;

@Repository
public interface FinancialRecordRepository extends CrudRepository<FinancialRecord, Long> {

	List<FinancialRecord> findByAccountingPeriodIdAndType(AccountingPeriodId accountingPeriodId, FinancialRecordType type);

	List<FinancialRecord> findByUserIdAndAccountingPeriodIdAndType(UserId userId,
																   AccountingPeriodId accountingPeriodId,
																   FinancialRecordType type,
																   Pageable pageable);

	List<FinancialRecord> findByAccountingPeriodId(AccountingPeriodId periodId);

	int countByUserIdAndAccountingPeriodIdAndType(UserId userId, AccountingPeriodId accountingPeriodId, FinancialRecordType type);
}
