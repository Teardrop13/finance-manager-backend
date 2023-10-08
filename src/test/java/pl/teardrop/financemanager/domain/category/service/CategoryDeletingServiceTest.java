package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryDeletingServiceTest {

	@Mock
	private CategoryRetrievingService categoryRetrievingService;
	@Mock
	private CategorySavingService categorySavingService;
	@Mock
	private CategoriesReorderingService categoriesReorderingService;
	private CategoryDeletingService categoryDeletingService;

	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		categoryDeletingService = new CategoryDeletingService(categoryRetrievingService, categorySavingService, categoriesReorderingService);
	}

	@Test
	void delete() {
		final FinancialRecordType type = FinancialRecordType.INCOME;
		final CategoryId categoryId = new CategoryId(1L);

		ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);

		when(categoryRetrievingService.getById(categoryId))
				.thenReturn(Optional.of(
						Category.builder()
								.id(categoryId.getId())
								.deleted(false)
								.userId(USER_ID)
								.type(type)
								.build()
				));

		when(categorySavingService.save(any(Category.class))).thenAnswer(returnsFirstArg());

		categoryDeletingService.delete(categoryId);

		verify(categorySavingService, times(1)).save(argument.capture());
		verify(categoriesReorderingService, times(1)).reorder(any(UserId.class), any(FinancialRecordType.class));
		assertTrue(argument.getValue().isDeleted());
	}

}