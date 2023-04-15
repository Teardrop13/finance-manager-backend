package io.github.teardrop13.financemanager.service;

import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.financemanager.model.Category;
import io.github.teardrop13.financemanager.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> getByUser(User user) {
		return categoryRepository.findByUserOrderByPriority(user).stream()
				.filter(c -> !c.isDeleted())
				.collect(Collectors.toList());
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
}
