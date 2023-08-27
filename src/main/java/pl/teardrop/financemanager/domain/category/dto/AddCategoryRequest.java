package pl.teardrop.financemanager.domain.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

@Getter
@Setter
@NoArgsConstructor
public class AddCategoryRequest {

	private String name;
	private FinancialRecordType type;
}
