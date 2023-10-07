package pl.teardrop.financemanager.domain.accountingperiod.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.authentication.user.service.UserUtils;
import pl.teardrop.financemanager.domain.accountingperiod.dto.AccountingPeriodDTO;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodMapper;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounting-periods")
@Slf4j
public class AccountingPeriodController {

	private final AccountingPeriodService accountingPeriodService;
	private final AccountingPeriodMapper accountingPeriodMapper;

	@GetMapping("/current")
	public AccountingPeriodDTO getCurrent() {
		UserId userId = UserUtils.currentUserId();
		AccountingPeriod current = accountingPeriodService.getCurrent(userId);
		return accountingPeriodMapper.toDTO(current);
	}

	@GetMapping("/next")
	public AccountingPeriodDTO getNext(@RequestParam long currentId) {
		UserId userId = UserUtils.currentUserId();
		AccountingPeriod next = accountingPeriodService.getNext(new AccountingPeriodId(currentId), userId);
		return accountingPeriodMapper.toDTO(next);
	}

	@GetMapping("/previous")
	public AccountingPeriodDTO getPrevious(@RequestParam Long currentId) {
		UserId userId = UserUtils.currentUserId();
		AccountingPeriod previous = accountingPeriodService.getPrevious(new AccountingPeriodId(currentId), userId);
		return accountingPeriodMapper.toDTO(previous);
	}

}
