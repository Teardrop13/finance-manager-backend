package pl.teardrop.financemanager.domain.accountingperiod.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.accountingperiod.dto.AccountingPeriodDTO;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounting-periods")
@Slf4j
public class AccountingPeriodController {

	private final AccountingPeriodService accountingPeriodService;

	@GetMapping("/current")
	public AccountingPeriodDTO getCurrent() {
		User user = UserUtils.currentUser();
		return accountingPeriodService.getCurrent(user).toDTO();
	}

	@GetMapping("/next")
	public AccountingPeriodDTO getNext(@RequestParam long currentId) {
		User user = UserUtils.currentUser();
		return accountingPeriodService.getNext(currentId, user).toDTO();
	}

	@GetMapping("/previous")
	public AccountingPeriodDTO getPrevious(@RequestParam Long currentId) {
		User user = UserUtils.currentUser();
		return accountingPeriodService.getPrevious(currentId, user).toDTO();
	}

}
