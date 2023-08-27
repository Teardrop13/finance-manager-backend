package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.financemanager.dto.SummaryDTO;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.service.SummaryCalculator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
@Slf4j
public class AnalysisController {

	private final SummaryCalculator summaryCalculator;

	@GetMapping("/summary")
	public List<SummaryDTO> getSummary(@RequestParam FinancialRecordType type, @RequestParam int periodId) {
		return summaryCalculator.getSummary(periodId, type);
	}

}
