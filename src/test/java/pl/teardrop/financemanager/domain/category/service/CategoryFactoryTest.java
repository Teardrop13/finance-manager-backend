package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryPriority;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryFactoryTest {

	@Mock
	private CategoryRetrievingService categoryRetrievingService;
	private CategoryFactory categoryFactory;
	private final UserId USER_ID = new UserId(1L);

	@BeforeEach
	void setUp() {
		categoryFactory = new CategoryFactory(categoryRetrievingService);
	}

	@Test
	void get_whenThereAreOtherCategories() {
		final CategoryPriority lastCategoryPriority = new CategoryPriority(2);
		final CategoryPriority expectedPriority = lastCategoryPriority.incremented();
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		when(categoryRetrievingService.getLast(addCategoryCommand.userId(),
											   addCategoryCommand.type()))
				.thenReturn(Optional.of(Category.builder().priority(lastCategoryPriority).build()));

		final Category category = categoryFactory.get(addCategoryCommand);

		assertEquals(addCategoryCommand.name(), category.getName());
		assertEquals(addCategoryCommand.userId(), category.getUserId());
		assertEquals(addCategoryCommand.type(), category.getType());
		assertEquals(expectedPriority, category.getPriority());
	}

	@Test
	void get_whenThereIsNoOtherCategories() {
		final CategoryPriority expectedPriority = new CategoryPriority(1);
		final AddCategoryCommand addCategoryCommand = new AddCategoryCommand(USER_ID,
																			 "House",
																			 FinancialRecordType.EXPENSE);

		when(categoryRetrievingService
					 .getLast(any(UserId.class),
							  any(FinancialRecordType.class)))
				.thenReturn(Optional.empty());

		final Category category = categoryFactory.get(addCategoryCommand);

		assertEquals(addCategoryCommand.name(), category.getName());
		assertEquals(expectedPriority, category.getPriority());
	}
}