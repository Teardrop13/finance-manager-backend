package pl.teardrop.financemanager.domain.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultCategoriesServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	private DefaultCategoriesService defaultCategoriesService;

	@BeforeEach
	void setUp() {
		defaultCategoriesService = new DefaultCategoriesService(categoryRepository);
	}

	@Test
	void addDefault() {
		UserId userId = new UserId(1L);
		ArgumentCaptor<Category> commandCaptor = ArgumentCaptor.forClass(Category.class);

		defaultCategoriesService.addDefaults(userId);

		verify(categoryRepository, atLeastOnce()).save(commandCaptor.capture());

		List<Category> savedCategories = commandCaptor.getAllValues();
		assertFalse(savedCategories.isEmpty());

		Category category = savedCategories.get(0);
		assertNotNull(category.getPriority());
		assertNotNull(category.getName());
		assertNotNull(category.getType());
	}

}