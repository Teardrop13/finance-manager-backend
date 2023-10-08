package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryRetrievingServiceTest {

	@Mock
	private CategoryRepository categoryRepository;
	private CategoryRetrievingService categoryRetrievingService;

	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		categoryRetrievingService = new CategoryRetrievingService(categoryRepository);
	}

	@Test
	void getLast() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		CategoryRetrievingService categoryRetrievingServiceMock = spy(categoryRetrievingService);

		doReturn(List.of(Category.builder()
								 .priority(new CategoryPriority(1))
								 .deleted(false)
								 .build(),

						 Category.builder()
								 .priority(new CategoryPriority(2))
								 .deleted(false)
								 .build())
		).when(categoryRetrievingServiceMock).getNotDeletedByUserAndType(USER_ID, type);

		Optional<Category> categoryOpt = categoryRetrievingServiceMock.getLast(USER_ID, type);
		assertTrue(categoryOpt.isPresent());
		assertEquals(new CategoryPriority(2), categoryOpt.get().getPriority());
	}

	@Test
	void getLast_whenAllCategoriesAreDeleted() {
		final FinancialRecordType type = FinancialRecordType.INCOME;

		CategoryRetrievingService categoryRetrievingServiceMock = spy(categoryRetrievingService);

		doReturn(new ArrayList<>())
				.when(categoryRetrievingServiceMock)
				.getNotDeletedByUserAndType(USER_ID, type);

		Optional<Category> categoryOpt = categoryRetrievingServiceMock.getLast(USER_ID, type);

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

		List<Category> categories = categoryRetrievingService.getNotDeletedByUserAndType(USER_ID, type);

		assertEquals(3, categories.size());
		assertFalse(categories.get(0).isDeleted());
		assertFalse(categories.get(1).isDeleted());
		assertFalse(categories.get(2).isDeleted());
	}
}