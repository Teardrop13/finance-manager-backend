package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.CategoryExistException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
								 .priority(1)
								 .deleted(false)
								 .build(),

						 Category.builder()
								 .priority(2)
								 .deleted(false)
								 .build())
		).when(categoryServiceMock).getNotDeletedByUserAndType(USER_ID, type);

		Optional<Category> categoryOpt = categoryServiceMock.getLast(USER_ID, type);
		assertTrue(categoryOpt.isPresent());
		assertEquals(2, categoryOpt.get().getPriority());
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
								.priority(1)
								.deleted(false)
								.build(),

						Category.builder()
								.priority(2)
								.deleted(true)
								.build(),

						Category.builder()
								.priority(2)
								.deleted(false)
								.build(),

						Category.builder()
								.priority(3)
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
		final int lastCategoryPriority = 2;
		final int expectedPriority = lastCategoryPriority + 1;
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
		final int lastCategoryPriority = 2;
		final int expectedPriority = lastCategoryPriority + 1;

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
		final int expectedPriority = 1;
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

	@Test
	void delete() {
		final FinancialRecordType type = FinancialRecordType.INCOME;
		final CategoryId categoryId = new CategoryId(1L);

		CategoryService categoryServiceMock = spy(categoryService);
		ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);

		when(categoryServiceMock.getById(categoryId))
				.thenReturn(Optional.of(
						Category.builder()
								.id(categoryId.getId())
								.deleted(false)
								.userId(USER_ID)
								.type(type)
								.build()
				));

		doAnswer(returnsFirstArg()).when(categoryServiceMock).save(any(Category.class));
		doNothing().when(categoryServiceMock).reorder(any(UserId.class), any(FinancialRecordType.class));

		categoryServiceMock.delete(categoryId);

		verify(categoryServiceMock, times(1)).save(argument.capture());
		verify(categoryServiceMock, times(1)).reorder(any(UserId.class), any(FinancialRecordType.class));
		assertTrue(argument.getValue().isDeleted());
	}

	@Test
	void reorder_whenOnePriorityIsMissing() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		CategoryService categoryServiceMock = spy(categoryService);
		ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);

		when(categoryServiceMock.getNotDeletedByUserAndType(USER_ID, type))
				.thenReturn(List.of(
						Category.builder()
								.priority(1)
								.deleted(false)
								.build(),

						Category.builder()
								.priority(3)
								.deleted(false)
								.build()
				));

		doAnswer(returnsFirstArg())
				.when(categoryServiceMock).save(any(Category.class));

		categoryServiceMock.reorder(USER_ID, type);

		verify(categoryServiceMock, times(2)).save(argument.capture());

		List<Category> savedCategories = argument.getAllValues();
		assertEquals(2, savedCategories.size());
		assertEquals(1, savedCategories.get(0).getPriority());
		assertEquals(2, savedCategories.get(1).getPriority());
	}
}