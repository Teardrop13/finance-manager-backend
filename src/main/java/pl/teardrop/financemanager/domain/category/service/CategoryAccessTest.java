package pl.teardrop.financemanager.domain.category.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryAccessTest {

	private final CategoryRepository categoryRepository;

	public boolean test(@NonNull CategoryId categoryId, Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		UserId userId = categoryRepository.findById(categoryId.getId())
				.map(Category::getUserId)
				.orElse(null);

		boolean result = Objects.equals(user.userId(), userId);

		log.info("Testing if categoryId={} belongs to userid={}, result={}", categoryId.getId(), user.getId(), result);

		return result;
	}

}
