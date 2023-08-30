package pl.teardrop.financemanager.domain.financialrecord.model;

import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.dto.CategorySummaryDTO;

import java.math.BigDecimal;

public record CategorySummary(BigDecimal amount, Category category) {

	public static final CategorySummary ZERO = new CategorySummary(BigDecimal.ZERO, null);

	public CategorySummary add(CategorySummary other) {
		return new CategorySummary(amount.add(other.amount),
								   other.category);
	}

	public CategorySummaryDTO toDto() {
		return new CategorySummaryDTO(amount, category.getName());
	}
}
