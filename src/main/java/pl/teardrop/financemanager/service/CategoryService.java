package pl.teardrop.financemanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> getByUser(User user) {
		return categoryRepository.findByUserOrderByPriority(user).stream()
				.filter(c -> !c.isDeleted())
				.toList();
	}

	public Optional<Category> getById(long id) {
		return categoryRepository.findById(id);
	}

	public Category save(Category category) {
		Category categoryAdded = categoryRepository.save(category);
		log.info("Saved category id={}, userId={}", categoryAdded.getId(), categoryAdded.getUser().getId());
		return categoryAdded;
	}

	public void delete(long id) {
		categoryRepository.findById(id).ifPresent(category -> {
			category.setDeleted(true);
			categoryRepository.save(category);
			log.info("Category marked deleted id={}, userId={}", category.getId(), category.getUser().getId());
		});
	}

	public Optional<Category> getByUserAndName(User user, String name) {
		return categoryRepository.getByUserAndName(user, name);
	}
}
