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
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	private CategoryService categoryService;

	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@Test
	void getLast() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		CategoryService categoryServiceMock = spy(categoryService);

		doReturn(List.of(Category.builder()
								 .priority(new CategoryPriority(1))
								 .deleted(false)
								 .build(),

						 Category.builder()
								 .priority(new CategoryPriority(2))
								 .deleted(false)
								 .build())
		).when(categoryServiceMock).getNotDeletedByUserAndType(USER_ID, type);

		Optional<Category> categoryOpt = categoryServiceMock.getLast(USER_ID, type);
		assertTrue(categoryOpt.isPresent());
		assertEquals(new CategoryPriority(2), categoryOpt.get().getPriority());
	}

	@Test
	void getLast_whenAllCategoriesAreDeleted() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		CategoryService categoryServiceMock = spy(categoryService);

		doReturn(new ArrayList<>())
				.when(categoryServiceMock)
				.getNotDeletedByUserAndType(USER_ID, type);

		Optional<Category> categoryOpt = categoryServiceMock.getLast(USER_ID, type);

		assertTrue(categoryOpt.isEmpty());
	}

	@Test
	void getNotDeletedByUserAndType_whenSomeCategoriesAreDeleted() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		when(categoryRepository.findByUserIdAndTypeOrderByPriority(USER_ID, type))
				.thenReturn(List.of(
						Category.builder()
								.priority(new CategoryPriority(1))
								.deleted(false)
								.build(),

						Category.builder()
								.priority(new CategoryPriority(2))
								.deleted(true)
								.build(),

						Category.builder()
								.priority(new CategoryPriority(2))
								.deleted(false)
								.build(),

						Category.builder()
								.priority(new CategoryPriority(3))
								.deleted(false)
								.build()
				));

		List<Category> categories = categoryService.getNotDeletedByUserAndType(USER_ID, type);

		assertEquals(3, categories.size());
		assertFalse(categories.get(0).isDeleted());
		assertFalse(categories.get(1).isDeleted());
		assertFalse(categories.get(2).isDeleted());
	}

	@Test
	void create_whenCategoryWithSuchNameIsNotPresent() throws CategoryExistException {
		final CategoryPriority lastCategoryPriority = new CategoryPriority(2);
		final CategoryPriority expectedPriority = lastCategoryPriority.incremented();
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		CategoryService categoryServiceMock = spy(categoryService);

		doReturn(Optional.empty())
				.when(categoryServiceMock)
				.getByUserAndTypeAndName(any(UserId.class),
										 any(FinancialRecordType.class),
										 any(String.class));

		doReturn(Optional.of(Category.builder().priority(lastCategoryPriority).build()))
				.when(categoryServiceMock)
				.getLast(addCategoryCommand.userId(),
						 addCategoryCommand.type());

		doAnswer(returnsFirstArg()).when(categoryServiceMock).save(any(Category.class));

		final Category addedCategory = categoryServiceMock.create(addCategoryCommand);

		assertEquals(addCategoryCommand.name(), addedCategory.getName());
		assertEquals(addCategoryCommand.userId(), addedCategory.getUserId());
		assertEquals(addCategoryCommand.type(), addedCategory.getType());
		assertEquals(expectedPriority, addedCategory.getPriority());
	}

	@Test
	void create_whenCategoryWithSuchNameIsPresent() {
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		CategoryService categoryServiceMock = spy(categoryService);

		doReturn(Optional.of(Category.builder().deleted(false).build()))
				.when(categoryServiceMock)
				.getByUserAndTypeAndName(any(UserId.class),
										 any(FinancialRecordType.class),
										 any(String.class));

		assertThrows(CategoryExistException.class, () -> categoryServiceMock.create(addCategoryCommand));
	}

	@Test
	void create_whenCategoryWithSuchNameIsPresentButDeleted() throws CategoryExistException {
		final CategoryPriority lastCategoryPriority = new CategoryPriority(2);
		final CategoryPriority expectedPriority = lastCategoryPriority.incremented();

		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		CategoryService categoryServiceMock = spy(categoryService);

		doReturn(Optional.of(
				Category.builder()
						.id(1L)
						.name(addCategoryCommand.name())
						.deleted(true)
						.userId(addCategoryCommand.userId())
						.type(addCategoryCommand.type())
						.build()
		))
				.when(categoryServiceMock)
				.getByUserAndTypeAndName(any(UserId.class),
										 any(FinancialRecordType.class),
										 any(String.class));

		doReturn(Optional.of(Category.builder().priority(lastCategoryPriority).build()))
				.when(categoryServiceMock)
				.getLast(any(UserId.class),
						 any(FinancialRecordType.class));

		doAnswer(returnsFirstArg()).when(categoryServiceMock).save(any(Category.class));

		final Category addedCategory = categoryServiceMock.create(addCategoryCommand);

		assertEquals(addCategoryCommand.name(), addedCategory.getName());
		assertEquals(expectedPriority, addedCategory.getPriority());
		assertFalse(addedCategory.isDeleted());
	}

	@Test
	void create_whenThereIsNoOtherCategories() throws CategoryExistException {
		final CategoryPriority expectedPriority = new CategoryPriority(1);
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		CategoryService categoryServiceMock = spy(categoryService);

		doReturn(Optional.empty())
				.when(categoryServiceMock)
				.getByUserAndTypeAndName(any(UserId.class),
										 any(FinancialRecordType.class),
										 any(String.class));

		doReturn(Optional.empty())
				.when(categoryServiceMock)
				.getLast(any(UserId.class),
						 any(FinancialRecordType.class));

		doAnswer(returnsFirstArg()).when(categoryServiceMock).save(any(Category.class));

		final Category addedCategory = categoryServiceMock.create(addCategoryCommand);

		assertEquals(addCategoryCommand.name(), addedCategory.getName());
		assertEquals(expectedPriority, addedCategory.getPriority());
	}
}