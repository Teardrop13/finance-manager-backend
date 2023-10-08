package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.CategoryExistException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddingCategoryServiceTest {

	@Mock
	private CategorySavingService categorySavingService;
	@Mock
	private CategoryRetrievingService categoryRetrievingService;
	@Mock
	private CategoryFactory categoryFactory;
	private AddingCategoryService addingCategoryService;

	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		addingCategoryService = new AddingCategoryService(categoryRetrievingService, categorySavingService, categoryFactory);
	}

	@Test
	void create_whenCategoryWithSuchNameIsNotPresent() throws CategoryExistException {
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		when(categoryRetrievingService
					 .getByUserAndTypeAndName(any(UserId.class),
											  any(FinancialRecordType.class),
											  any(String.class)))
				.thenReturn(Optional.empty());

		addingCategoryService.add(addCategoryCommand);

		verify(categoryFactory, times(1)).get(any(AddCategoryCommand.class));
		verify(categorySavingService, times(1)).save(any());
	}

	@Test
	void create_whenCategoryWithSuchNameIsPresent() {
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		when(categoryRetrievingService
					 .getByUserAndTypeAndName(any(UserId.class),
											  any(FinancialRecordType.class),
											  any(String.class)))
				.thenReturn(Optional.of(Category.builder().deleted(false).build()));

		assertThrows(CategoryExistException.class, () -> addingCategoryService.add(addCategoryCommand));
	}

	@Test
	void create_whenCategoryWithSuchNameIsPresentButDeleted() throws CategoryExistException {
		final CategoryPriority lastCategoryPriority = new CategoryPriority(2);
		final CategoryPriority expectedPriority = lastCategoryPriority.incremented();

		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		when(categoryRetrievingService
					 .getByUserAndTypeAndName(any(UserId.class),
											  any(FinancialRecordType.class),
											  any(String.class)))
				.thenReturn(Optional.of(
						Category.builder()
								.id(1L)
								.name(addCategoryCommand.name())
								.deleted(true)
								.userId(addCategoryCommand.userId())
								.type(addCategoryCommand.type())
								.build()
				));

		when(categoryRetrievingService
					 .getLast(any(UserId.class),
							  any(FinancialRecordType.class)))
				.thenReturn(Optional.of(Category.builder().priority(lastCategoryPriority).build()));

		when(categorySavingService.save(any(Category.class))).thenAnswer(returnsFirstArg());

		final Category addedCategory = addingCategoryService.add(addCategoryCommand);

		assertEquals(addCategoryCommand.name(), addedCategory.getName());
		assertEquals(expectedPriority, addedCategory.getPriority());
		assertFalse(addedCategory.isDeleted());
		verify(categorySavingService, times(1)).save(any());
	}
}