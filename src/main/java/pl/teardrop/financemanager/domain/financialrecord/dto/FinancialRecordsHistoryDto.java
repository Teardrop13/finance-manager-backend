package pl.teardrop.financemanager.domain.financialrecord.dto;

import lombok.Value;

import java.util.List;

@Value
public class FinancialRecordsHistoryDto {

	int count;
	List<FinancialRecordDTO> records;
}
