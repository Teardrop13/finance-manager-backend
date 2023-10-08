package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriesReorderingServiceTest {

	@Mock
	private CategoriesReorderingValidator categoriesReorderingValidator;
	@Mock
	private CategoryService categoryService;

	private CategoriesReorderingService categoriesReorderingService;
	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		categoriesReorderingService = new CategoriesReorderingService(categoryService, categoriesReorderingValidator);
	}

	@Test
	void reorder_whenOnePriorityIsMissing() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);

		when(categoryService.getNotDeletedByUserAndType(USER_ID, type))
				.thenReturn(List.of(
						Category.builder()
								.priority(new CategoryPriority(1))
								.deleted(false)
								.build(),

						Category.builder()
								.priority(new CategoryPriority(3))
								.deleted(false)
								.build()
				));

		doAnswer(returnsFirstArg())
				.when(categoryService).save(any(Category.class));

		categoriesReorderingService.reorder(USER_ID, type);

		verify(categoryService, times(2)).save(argument.capture());

		List<Category> savedCategories = argument.getAllValues();
		assertEquals(2, savedCategories.size());
		assertEquals(new CategoryPriority(1), savedCategories.get(0).getPriority());
		assertEquals(new CategoryPriority(2), savedCategories.get(1).getPriority());
	}

}