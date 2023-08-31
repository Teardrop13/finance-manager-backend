package pl.teardrop.financemanager.domain.financialrecord.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.financialrecord.dto.CategorySummaryDTO;
import pl.teardrop.financemanager.domain.financialrecord.exception.AccountingPeriodNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.model.AccountingPeriodSummary;
import pl.teardrop.financemanager.domain.financialrecord.model.CategorySummary;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;
import pl.teardrop.financemanager.domain.financialrecord.service.SummaryCalculator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
@Slf4j
public class AnalysisController {

	private final SummaryCalculator summaryCalculator;

	@GetMapping("/summary/category")
	public ResponseEntity<List<CategorySummaryDTO>> getSummaryByCategory(@RequestParam FinancialRecordType type, @RequestParam long periodId) {
		List<CategorySummaryDTO> summary = summaryCalculator.getSummaryByCategory(UserUtils.currentUserId(),
																				  new AccountingPeriodId(periodId),
																				  type)
				.stream()
				.map(CategorySummary::toDto)
				.toList();
		return ResponseEntity.ok(summary);
	}

	@GetMapping("/summary/record-type")
	public ResponseEntity<Object> getSummaryByRecordType(@RequestParam long periodId) {
		try {
			AccountingPeriodSummary summary = summaryCalculator.getAccountingPeriodSummary(new AccountingPeriodId(periodId));
			return ResponseEntity.ok(summary.toDto());
		} catch (AccountingPeriodNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Accounting period with id=%s not found".formatted(periodId));
		}
	}
}
