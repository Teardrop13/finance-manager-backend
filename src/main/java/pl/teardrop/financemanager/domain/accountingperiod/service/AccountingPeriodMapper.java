package pl.teardrop.financemanager.domain.accountingperiod.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.accountingperiod.dto.AccountingPeriodDTO;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;

@Service
@RequiredArgsConstructor
public class AccountingPeriodMapper {

	public AccountingPeriodDTO toDTO(AccountingPeriod accountingPeriod) {
		return new AccountingPeriodDTO(accountingPeriod.getId(),
									   accountingPeriod.getStartsOn(),
									   accountingPeriod.getEndsOn());
	}
}
