package pl.teardrop.financemanager.domain.accountingperiod.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.accountingperiod.exception.PeriodExistsException;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.repository.AccountingPeriodRepository;

@Service
@AllArgsConstructor
public class AccountingPeriodValidator {

	private final AccountingPeriodRepository accountingPeriodRepository;

	public void validate(AccountingPeriod period) throws PeriodExistsException {
		if (accountingPeriodRepository.findFirstByDate(period.getStartsOn(), period.getUserId()).isPresent()
			|| accountingPeriodRepository.findFirstByDate(period.getEndsOn(), period.getUserId()).isPresent()) {
			throw new PeriodExistsException("Period overlaps with another period");
		}
	}
}
