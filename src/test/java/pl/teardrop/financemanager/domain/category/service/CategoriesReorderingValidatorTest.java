package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoriesCommand;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoryCommand;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriesReorderingValidatorTest {

	@Mock
	private CategoryRetrievingService categoryRetrievingService;
	private CategoriesReorderingValidator validator;

	private final UserId userId = new UserId(1L);
	private final FinancialRecordType type = FinancialRecordType.EXPENSE;

	@BeforeEach
	void setUp() {
		validator = new CategoriesReorderingValidator(categoryRetrievingService);
	}

	@Test
	void validate_whenCommandsSizeMatchExistingCategories() {
		CategoryId categoryId1 = new CategoryId(1L);
		CategoryId categoryId2 = new CategoryId(2L);
		CategoryId categoryId3 = new CategoryId(3L);

		when(categoryRetrievingService.getNotDeletedByUserAndType(userId, type)).thenReturn(List.of(
				Category.builder().id(categoryId1.getId()).priority(new CategoryPriority(1)).build(),
				Category.builder().id(categoryId2.getId()).priority(new CategoryPriority(2)).build(),
				Category.builder().id(categoryId3.getId()).priority(new CategoryPriority(3)).build()
		));

		ReorderCategoriesCommand command = new ReorderCategoriesCommand(userId, type, List.of(
				new ReorderCategoryCommand(categoryId1, new CategoryPriority(1)),
				new ReorderCategoryCommand(categoryId2, new CategoryPriority(3)),
				new ReorderCategoryCommand(categoryId3, new CategoryPriority(2))
		));

		assertTrue(validator.validate(command));
	}

	@Test
	void validate_whenCommandsSizeDoesNotContainAllRequiredIds() {
		CategoryId categoryId1 = new CategoryId(1L);
		CategoryId categoryId2 = new CategoryId(2L);
		CategoryId categoryId3 = new CategoryId(3L);

		when(categoryRetrievingService.getNotDeletedByUserAndType(userId, type)).thenReturn(List.of(
				Category.builder().id(categoryId1.getId()).priority(new CategoryPriority(1)).build(),
				Category.builder().id(categoryId2.getId()).priority(new CategoryPriority(2)).build(),
				Category.builder().id(categoryId3.getId()).priority(new CategoryPriority(3)).build()
		));

		ReorderCategoriesCommand command = new ReorderCategoriesCommand(userId, type, List.of(
				new ReorderCategoryCommand(categoryId1, new CategoryPriority(1)),
				new ReorderCategoryCommand(categoryId3, new CategoryPriority(2))
		));

		assertFalse(validator.validate(command));
	}

	@Test
	void validate_whenCommandHasDuplicateId() {
		CategoryId categoryId1 = new CategoryId(1L);
		CategoryId categoryId2 = new CategoryId(2L);
		CategoryId categoryId3 = new CategoryId(3L);

		when(categoryRetrievingService.getNotDeletedByUserAndType(userId, type)).thenReturn(List.of(
				Category.builder().id(categoryId1.getId()).priority(new CategoryPriority(1)).build(),
				Category.builder().id(categoryId2.getId()).priority(new CategoryPriority(2)).build(),
				Category.builder().id(categoryId3.getId()).priority(new CategoryPriority(3)).build()
		));

		ReorderCategoriesCommand command = new ReorderCategoriesCommand(userId, type, List.of(
				new ReorderCategoryCommand(categoryId1, new CategoryPriority(1)),
				new ReorderCategoryCommand(categoryId2, new CategoryPriority(3)),
				new ReorderCategoryCommand(categoryId2, new CategoryPriority(2))
		));

		assertFalse(validator.validate(command));
	}

	@Test
	void validate_whenCommandHasDuplicatePriority() {
		CategoryId categoryId1 = new CategoryId(1L);
		CategoryId categoryId2 = new CategoryId(2L);
		CategoryId categoryId3 = new CategoryId(3L);

		when(categoryRetrievingService.getNotDeletedByUserAndType(userId, type)).thenReturn(List.of(
				Category.builder().id(categoryId1.getId()).priority(new CategoryPriority(1)).build(),
				Category.builder().id(categoryId2.getId()).priority(new CategoryPriority(2)).build(),
				Category.builder().id(categoryId3.getId()).priority(new CategoryPriority(3)).build()
		));

		ReorderCategoriesCommand command = new ReorderCategoriesCommand(userId, type, List.of(
				new ReorderCategoryCommand(categoryId1, new CategoryPriority(1)),
				new ReorderCategoryCommand(categoryId2, new CategoryPriority(2)),
				new ReorderCategoryCommand(categoryId3, new CategoryPriority(2))
		));

		assertFalse(validator.validate(command));
	}

	@Test
	void validate_whenCommandHasNonexistentId() {
		CategoryId categoryId1 = new CategoryId(1L);
		CategoryId categoryId2 = new CategoryId(2L);
		CategoryId categoryId3 = new CategoryId(3L);
		CategoryId InvalidCategoryId = new CategoryId(4L);

		when(categoryRetrievingService.getNotDeletedByUserAndType(userId, type)).thenReturn(List.of(
				Category.builder().id(categoryId1.getId()).priority(new CategoryPriority(1)).build(),
				Category.builder().id(categoryId2.getId()).priority(new CategoryPriority(2)).build(),
				Category.builder().id(categoryId3.getId()).priority(new CategoryPriority(3)).build()
		));

		ReorderCategoriesCommand command = new ReorderCategoriesCommand(userId, type, List.of(
				new ReorderCategoryCommand(categoryId1, new CategoryPriority(1)),
				new ReorderCategoryCommand(categoryId2, new CategoryPriority(3)),
				new ReorderCategoryCommand(InvalidCategoryId, new CategoryPriority(2))
		));

		assertFalse(validator.validate(command));
	}
}