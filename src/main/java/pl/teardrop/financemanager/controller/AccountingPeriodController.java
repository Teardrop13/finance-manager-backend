package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.dto.AccountingPeriodDTO;
import pl.teardrop.financemanager.model.AccountingPeriod;
import pl.teardrop.financemanager.service.AccountingPeriodService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounting-period")
@Slf4j
public class AccountingPeriodController {

	private final AccountingPeriodService accountingPeriodService;

	@GetMapping("/current")
	public AccountingPeriodDTO getCurrent() {
		return UserUtils.currentUser()
				.map(accountingPeriodService::getCurrent)
				.map(AccountingPeriod::toDTO)
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's AccountingPeriod. User not found."));
	}

	@GetMapping("/next")
	public AccountingPeriodDTO getNext(@RequestParam long currentId) {
		return UserUtils.currentUser()
				.map(user -> accountingPeriodService.getNext(currentId, user))
				.map(AccountingPeriod::toDTO)
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's AccountingPeriod. User not found."));
	}

	@GetMapping("/previous")
	public AccountingPeriodDTO getPrevious(@RequestParam Long currentId) {
		return UserUtils.currentUser()
				.map(user -> accountingPeriodService.getPrevious(currentId, user))
				.map(AccountingPeriod::toDTO)
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's AccountingPeriod. User not found."));
	}

}
