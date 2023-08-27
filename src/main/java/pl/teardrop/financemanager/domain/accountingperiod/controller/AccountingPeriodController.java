package pl.teardrop.financemanager.domain.accountingperiod.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.accountingperiod.dto.AccountingPeriodDTO;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounting-periods")
@Slf4j
public class AccountingPeriodController {

	private final AccountingPeriodService accountingPeriodService;

	@GetMapping("/current")
	public AccountingPeriodDTO getCurrent() {
		UserId userId = UserUtils.currentUserId();
		return accountingPeriodService.getCurrent(userId).toDTO();
	}

	@GetMapping("/next")
	public AccountingPeriodDTO getNext(@RequestParam long currentId) {
		UserId userId = UserUtils.currentUserId();
		return accountingPeriodService.getNext(new AccountingPeriodId(currentId), userId).toDTO();
	}

	@GetMapping("/previous")
	public AccountingPeriodDTO getPrevious(@RequestParam Long currentId) {
		UserId userId = UserUtils.currentUserId();
		return accountingPeriodService.getPrevious(new AccountingPeriodId(currentId), userId).toDTO();
	}

}
