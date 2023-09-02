package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;

@Service
@AllArgsConstructor
public class FinancialRecordMapper {

	private CategoryService categoryService;

	public FinancialRecordDTO toDTO(FinancialRecord financialRecord) {
		Category category = categoryService.getById(financialRecord.getCategoryId()).orElseThrow(() -> new RuntimeException("Cannot create FinancialRecordDto. Category not found for categoryId=" + financialRecord.getCategoryId().getId()));

		return new FinancialRecordDTO(financialRecord.getId(),
									  financialRecord.getDescription(),
									  financialRecord.getAmount(),
									  category.getName(),
									  financialRecord.getType(),
									  financialRecord.getTransactionDate());
	}

}
