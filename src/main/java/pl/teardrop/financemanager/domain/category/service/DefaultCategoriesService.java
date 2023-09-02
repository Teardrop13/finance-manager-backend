package pl.teardrop.financemanager.domain.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.common.JsonUtil;

@Service
@AllArgsConstructor
@Slf4j
public class DefaultCategoriesService {

	private final CategoryService categoryService;

	@PreAuthorize("#userId.getId() == authentication.principal.getId()")
	public void addDefaults(UserId userId) {
		new JsonUtil().loadDefaultCategories().forEach(category -> {
			category.setUserId(userId);
			categoryService.save(category);
		});
		log.info("Added default categories for userId={}", userId.getId());
	}
}
