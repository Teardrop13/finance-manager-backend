package pl.teardrop.financemanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.dto.SummaryDTO;
import pl.teardrop.financemanager.model.FinancialRecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SummaryCalculator {

	private final FinancialRecordService financialRecordService;

	public List<SummaryDTO> getSummary(int periodId, FinancialRecordType type) {
		return new ArrayList<>(financialRecordService.getByPeriodIdAndType(periodId, type).stream()
									   .map(r -> new SummaryDTO(r.getAmount(), r.getCategory().getName()))
									   .collect(Collectors.groupingBy(SummaryDTO::category,
																	  Collectors.reducing(SummaryDTO.ZERO, SummaryDTO::add)))
									   .values());
	}

}
