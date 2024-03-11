package pl.teardrop.financemanager.domain.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.financemanager.common.JsonUtil;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;

@Service
@AllArgsConstructor
@Slf4j
public class AddingDefaultCategoriesService {

	private final CategoryRepository categoryRepository;

	public void add(UserId userId) {
		new JsonUtil().loadDefaultCategories().forEach(category -> {
			category.setUserId(userId);
			categoryRepository.save(category);
		});
		log.info("Added default categories for userId={}", userId.getId());
	}
}
