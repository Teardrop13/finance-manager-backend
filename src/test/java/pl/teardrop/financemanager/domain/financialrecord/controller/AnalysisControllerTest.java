package pl.teardrop.financemanager.domain.financialrecord.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.financialrecord.exception.AccountingPeriodNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.model.AccountingPeriodSummary;
import pl.teardrop.financemanager.domain.financialrecord.model.CategorySummary;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;
import pl.teardrop.financemanager.domain.financialrecord.service.SummaryCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AnalysisController.class)
@AutoConfigureMockMvc(addFilters = false) // disable spring security (unauthorized error)
class AnalysisControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private SummaryCalculator summaryCalculator;
	@MockBean
	private Authentication authentication;

	@Test
	void getSummaryByCategory() throws Exception {
		long periodId = 1;
		long userId = 1;
		FinancialRecordType recordType = FinancialRecordType.EXPENSE;
		String categoryName = "house";
		Category category = Category.builder()
				.name(categoryName)
				.build();

		when(authentication.getPrincipal()).thenReturn(User.builder().id(userId).build());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		when(summaryCalculator.getSummaryByCategory(new UserId(userId), new AccountingPeriodId(periodId), recordType))
				.thenReturn(List.of(new CategorySummary(new BigDecimal("15.30"), category)));

		mvc.perform(MockMvcRequestBuilders.get("/api/analysis/summary/category")
							.param("periodId", String.valueOf(periodId))
							.param("type", recordType.name().toLowerCase())
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].amount", is(15.3)))
				.andExpect(jsonPath("$[0].category", is(categoryName)));
	}

	@Test
	void getSummaryByRecordType() throws Exception {
		long periodId = 1;

		AccountingPeriod accountingPeriod = AccountingPeriod.builder()
				.startsOn(LocalDate.of(2023, 8, 1))
				.endsOn(LocalDate.of(2023, 8, 31))
				.id(periodId)
				.build();

		when(summaryCalculator.getAccountingPeriodSummary(new AccountingPeriodId(periodId)))
				.thenReturn(new AccountingPeriodSummary(new BigDecimal("10"), new BigDecimal("20.50"), accountingPeriod));

		mvc.perform(MockMvcRequestBuilders.get("/api/analysis/summary/record-type")
							.param("periodId", String.valueOf(periodId))
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.income", is(10)))
				.andExpect(jsonPath("$.expense", is(20.50)))
				.andExpect(jsonPath("$.startsOn", is("01-08-2023")))
				.andExpect(jsonPath("$.endsOn", is("31-08-2023")))
				.andExpect(jsonPath("$.accountingPeriodId", is(1)));
	}

	@Test
	void getSummaryByRecordType_whenRequestedPeriodDoesNotExist() throws Exception {
		long periodId = 1;

		when(summaryCalculator.getAccountingPeriodSummary(new AccountingPeriodId(periodId)))
				.thenThrow(AccountingPeriodNotFoundException.class);

		mvc.perform(MockMvcRequestBuilders.get("/api/analysis/summary/record-type")
							.param("periodId", String.valueOf(periodId))
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}