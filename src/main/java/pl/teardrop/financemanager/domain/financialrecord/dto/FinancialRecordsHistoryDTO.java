package pl.teardrop.financemanager.domain.financialrecord.dto;

import java.util.List;

public record FinancialRecordsHistoryDTO(int count,
										 List<FinancialRecordDTO> records) {

}
