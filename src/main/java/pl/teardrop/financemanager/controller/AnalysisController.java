package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.dto.SummaryDTO;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.service.CategoryService;
import pl.teardrop.financemanager.service.FinancialRecordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
@Slf4j
public class AnalysisController {

	private final FinancialRecordService recordService;

	private final CategoryService categoryService;

	@GetMapping("/summary")
	public List<SummaryDTO> getSummary(@RequestParam FinancialRecordType type, @RequestParam int periodId) {
		return UserUtils.currentUser()
				.map(user -> recordService.getSummary(periodId, type))
				.orElseThrow(() -> new UserNotFoundException("Could not calculate summary. User not found."));
	}

}
