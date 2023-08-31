package pl.teardrop.financemanager.domain.financialrecord.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.service.AccountingPeriodService;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.exception.AccountingPeriodNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.model.AccountingPeriodSummary;
import pl.teardrop.financemanager.domain.financialrecord.model.CategorySummary;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryCalculatorTest {

	@Mock
	private FinancialRecordService financialRecordService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private AccountingPeriodService accountingPeriodService;
	private SummaryCalculator summaryCalculator;

	@BeforeEach
	void setUp() {
		summaryCalculator = new SummaryCalculator(financialRecordService, categoryService, accountingPeriodService);
	}

	@Test
	void getSummaryByCategory() {
		AccountingPeriodId periodId = new AccountingPeriodId(1L);
		UserId userId = new UserId(1L);

		CategoryId categoryIdHouse = new CategoryId(1L);
		CategoryId categoryIdMedia = new CategoryId(2L);
		CategoryId categoryIdHealth = new CategoryId(3L);

		Category categoryHouse = Category.builder()
				.id(1L)
				.name("House")
				.build();
		Category categoryMedia = Category.builder()
				.id(2L)
				.name("Media")
				.build();
		Category categoryHealth = Category.builder()
				.id(3L)
				.name("Health")
				.build();

		when(categoryService.getByUserAndType(userId, FinancialRecordType.EXPENSE))
				.thenReturn(List.of(categoryHouse, categoryMedia, categoryHealth));

		when(financialRecordService.getByPeriodIdAndType(periodId, FinancialRecordType.EXPENSE))
				.thenReturn(List.of(
						FinancialRecord.builder()
								.amount(new BigDecimal("10"))
								.type(FinancialRecordType.EXPENSE)
								.categoryId(categoryIdHouse)
								.build(),
						FinancialRecord.builder()
								.amount(new BigDecimal("15"))
								.type(FinancialRecordType.EXPENSE)
								.categoryId(categoryIdMedia)
								.build(),
						FinancialRecord.builder()
								.amount(new BigDecimal("20.2"))
								.type(FinancialRecordType.EXPENSE)
								.categoryId(categoryIdHealth)
								.build(),
						FinancialRecord.builder()
								.amount(new BigDecimal("30.25"))
								.type(FinancialRecordType.EXPENSE)
								.categoryId(categoryIdMedia)
								.build()
				));

		List<CategorySummary> expectedSummary = List.of(new CategorySummary(new BigDecimal("10"), categoryHouse),
														new CategorySummary(new BigDecimal("45.25"), categoryMedia),
														new CategorySummary(new BigDecimal("20.2"), categoryHealth));

		List<CategorySummary> calculatedSummary = summaryCalculator.getSummaryByCategory(userId, periodId, FinancialRecordType.EXPENSE);

		Assertions.assertEquals(expectedSummary.size(), calculatedSummary.size(), "Summary list has wrong size");
		Assertions.assertTrue(expectedSummary.containsAll(calculatedSummary) && calculatedSummary.containsAll(expectedSummary), "Summary list has wrong amounts");
	}

	@Test
	void getAccountingPeriodSummary() throws AccountingPeriodNotFoundException {
		AccountingPeriodId periodId = new AccountingPeriodId(1L);
		AccountingPeriod accountingPeriod = AccountingPeriod.builder()
				.id(periodId.getId())
				.build();

		when(financialRecordService.getByPeriodId(periodId)).thenReturn(List.of(
				FinancialRecord.builder()
						.amount(new BigDecimal("10"))
						.type(FinancialRecordType.INCOME)
						.build(),
				FinancialRecord.builder()
						.amount(new BigDecimal("15"))
						.type(FinancialRecordType.EXPENSE)
						.build(),
				FinancialRecord.builder()
						.amount(new BigDecimal("20.2"))
						.type(FinancialRecordType.INCOME)
						.build(),
				FinancialRecord.builder()
						.amount(new BigDecimal("30.25"))
						.type(FinancialRecordType.INCOME)
						.build(),
				FinancialRecord.builder()
						.amount(new BigDecimal("15.05"))
						.type(FinancialRecordType.EXPENSE)
						.build()
		));

		when(accountingPeriodService.getById(periodId)).thenReturn(Optional.of(accountingPeriod));

		AccountingPeriodSummary expectedSummary = new AccountingPeriodSummary(new BigDecimal("60.45"), new BigDecimal("30.05"), accountingPeriod);

		AccountingPeriodSummary calculatedSummary = summaryCalculator.getAccountingPeriodSummary(periodId);

		Assertions.assertEquals(expectedSummary.income(), calculatedSummary.income(), "Calculated and expected summary have different income amounts");
		Assertions.assertEquals(expectedSummary.expense(), calculatedSummary.expense(), "Calculated and expected summary have different expense amounts");
		Assertions.assertEquals(expectedSummary.accountingPeriod(), calculatedSummary.accountingPeriod(), "Calculated and expected summary have different accounting period assigned");
	}

	@Test
	void getAccountingPeriodSummary_whenOneTypeIsMissing() throws AccountingPeriodNotFoundException {
		AccountingPeriodId periodId = new AccountingPeriodId(1L);
		AccountingPeriod accountingPeriod = AccountingPeriod.builder()
				.id(periodId.getId())
				.build();

		when(financialRecordService.getByPeriodId(periodId)).thenReturn(List.of(
				FinancialRecord.builder()
						.amount(new BigDecimal("15"))
						.type(FinancialRecordType.EXPENSE)
						.build(),
				FinancialRecord.builder()
						.amount(new BigDecimal("15.05"))
						.type(FinancialRecordType.EXPENSE)
						.build()
		));

		when(accountingPeriodService.getById(periodId)).thenReturn(Optional.of(accountingPeriod));

		AccountingPeriodSummary expectedSummary = new AccountingPeriodSummary(new BigDecimal("0"), new BigDecimal("30.05"), accountingPeriod);

		AccountingPeriodSummary calculatedSummary = summaryCalculator.getAccountingPeriodSummary(periodId);

		Assertions.assertEquals(expectedSummary.income(), calculatedSummary.income(), "Calculated and expected summary have different income amounts");
		Assertions.assertEquals(expectedSummary.expense(), calculatedSummary.expense(), "Calculated and expected summary have different expense amounts");
		Assertions.assertEquals(expectedSummary.accountingPeriod(), calculatedSummary.accountingPeriod(), "Calculated and expected summary have different accounting period assigned");
	}
}