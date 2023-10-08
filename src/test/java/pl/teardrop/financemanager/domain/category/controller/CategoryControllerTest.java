package pl.teardrop.financemanager.domain.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.teardrop.authentication.user.domain.User;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoriesRequest;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoryCommand;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoryRequest;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.category.service.AddingCategoryService;
import pl.teardrop.financemanager.domain.category.service.CategoriesReorderingService;
import pl.teardrop.financemanager.domain.category.service.CategoryDeletingService;
import pl.teardrop.financemanager.domain.category.service.CategoryMapper;
import pl.teardrop.financemanager.domain.category.service.CategoryRetrievingService;
import pl.teardrop.financemanager.domain.category.service.CategorySavingService;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private Authentication authentication;
	@MockBean
	private CategoryRetrievingService categoryRetrievingService;
	@MockBean
	private CategoryDeletingService categoryDeletingService;
	@MockBean
	private CategoryMapper categoryMapper;
	@MockBean
	private CategoriesReorderingService categoriesReorderingService;
	@MockBean
	private CategorySavingService categorySavingService;
	@MockBean
	private AddingCategoryService addingCategoryService;
	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		when(authentication.getPrincipal()).thenReturn(User.builder().id(USER_ID.getId()).build());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	void getCategories_whenTypeIsIncome() throws Exception {
		final FinancialRecordType type = FinancialRecordType.INCOME;
		final String categoryName = "salary";
		final long categoryId = 1;
		final CategoryPriority categoryPriority = new CategoryPriority(1);

		doAnswer(InvocationOnMock::callRealMethod).when(categoryMapper).toDTO(any(Category.class));

		when(categoryRetrievingService.getNotDeletedByUserAndType(any(UserId.class), any(FinancialRecordType.class))).thenReturn(List.of(
				Category.builder()
						.name(categoryName)
						.id(categoryId)
						.priority(categoryPriority)
						.build()
		));

		mvc.perform(MockMvcRequestBuilders.get("/api/categories/%s".formatted(type.getTextValue()))
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is((int) categoryId)))
				.andExpect(jsonPath("$[0].priority", is(categoryPriority.getValue())))
				.andExpect(jsonPath("$[0].name", is(categoryName)));
	}

	@Test
	void getCategories_whenTypeIsIncorrect() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/categories/aaa")
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void save() throws Exception {
		final UpdateCategoryRequest updateCategoryRequest1 = new UpdateCategoryRequest(1L, 1, "House");
		final UpdateCategoryRequest updateCategoryRequest2 = new UpdateCategoryRequest(2L, 2, "Car");
		final UpdateCategoriesRequest updateCategoriesRequest = new UpdateCategoriesRequest(List.of(
				updateCategoryRequest1,
				updateCategoryRequest2
		));

		ArgumentCaptor<UpdateCategoryCommand> commandCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

		mvc.perform(MockMvcRequestBuilders.put("/api/categories")
							.content(new ObjectMapper().writeValueAsString(updateCategoriesRequest))
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		verify(categorySavingService, times(2)).update(commandCaptor.capture());

		List<UpdateCategoryCommand> capturedCommands = commandCaptor.getAllValues();
		assertEquals(2, capturedCommands.size());

		UpdateCategoryCommand command1 = capturedCommands.get(0);
		assertEquals(updateCategoryRequest1.id(), command1.id().getId());
		assertEquals(updateCategoryRequest1.priority(), command1.priority().getValue());
		assertEquals(updateCategoryRequest1.name(), command1.name());

		UpdateCategoryCommand command2 = capturedCommands.get(1);
		assertEquals(updateCategoryRequest2.id(), command2.id().getId());
		assertEquals(updateCategoryRequest2.priority(), command2.priority().getValue());
		assertEquals(updateCategoryRequest2.name(), command2.name());
	}

	@Test
	void save_whenIdIsWrong() throws Exception {
		final UpdateCategoryRequest updateCategoryRequest1 = new UpdateCategoryRequest(1L, 1, "House");
		final UpdateCategoriesRequest updateCategoriesRequest = new UpdateCategoriesRequest(List.of(updateCategoryRequest1));

		doThrow(CategoryNotFoundException.class).when(categorySavingService).update(any(UpdateCategoryCommand.class));

		mvc.perform(MockMvcRequestBuilders.put("/api/categories")
							.content(new ObjectMapper().writeValueAsString(updateCategoriesRequest))
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}