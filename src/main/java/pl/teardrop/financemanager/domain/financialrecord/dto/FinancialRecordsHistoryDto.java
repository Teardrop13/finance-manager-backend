package pl.teardrop.financemanager.domain.financialrecord.dto;

import java.util.List;

public record FinancialRecordsHistoryDto(int count,
										 List<FinancialRecordDTO> records) {

}
