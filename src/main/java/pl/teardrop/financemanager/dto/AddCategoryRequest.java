package pl.teardrop.financemanager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.teardrop.financemanager.model.FinancialRecordType;

@Getter
@Setter
@NoArgsConstructor
public class AddCategoryRequest {

	private String name;
	private FinancialRecordType type;
}
