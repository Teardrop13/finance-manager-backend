package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.controller.exceptions.AddCategoryException;
import pl.teardrop.financemanager.dto.AddCategoryRequest;
import pl.teardrop.financemanager.dto.CategoryDTO;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.service.CategoryService;
import pl.teardrop.financemanager.service.FinancialRecordService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

	private final FinancialRecordService recordService;

	private final CategoryService categoryService;

	@GetMapping("/income")
	public List<CategoryDTO> getIncomeCategories() {
		return UserUtils.currentUser()
				.map(user -> categoryService.getByUser(user, FinancialRecordType.INCOME).stream()
						.filter(category -> !category.isDeleted())
						.map(Category::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's income categories. User not found."));
	}

	@GetMapping("/expense")
	public List<CategoryDTO> getExpenseCategories() {
		return UserUtils.currentUser()
				.map(user -> categoryService.getByUser(user, FinancialRecordType.EXPENSE).stream()
						.filter(category -> !category.isDeleted())
						.map(Category::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's expense categories. User not found."));
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public void save(@RequestBody List<CategoryDTO> categories) {
		categories.forEach(updatedCategory -> {
			categoryService.getById(updatedCategory.getId())
					.ifPresent(category -> {
						category.setName(updatedCategory.getName());
						category.setPriority(updatedCategory.getPriority());
						categoryService.save(category);
					});
		});
	}

	@PostMapping
	public ResponseEntity<CategoryDTO> add(@RequestBody AddCategoryRequest addRequest) {
		User user = UserUtils.currentUser().orElseThrow(() -> new UserNotFoundException("User not found"));

		Optional<Category> categoryOptional = categoryService.getByUserAndTypeAndName(user, addRequest.getType(), addRequest.getName());
		if (categoryOptional.isPresent()) {
			Category existingCategory = categoryOptional.get();
			log.info("Category with name \"{}\" exists", addRequest.getName());

			if (existingCategory.isDeleted()) {
				existingCategory.setName(addRequest.getName());
				existingCategory.setDeleted(false);
				categoryService.getLast(existingCategory.getUser(), existingCategory.getType()).ifPresent(last -> {
					existingCategory.setPriority(last.getPriority() + 1);
				});
				Category existingCategoryUpdated = categoryService.save(existingCategory);
				categoryService.reorder(user, addRequest.getType());
				return ResponseEntity.ok(existingCategoryUpdated.toDTO());
			} else {
				throw new AddCategoryException("Category with name " + addRequest.getName() + " exists");
			}
		} else {
			int lastCategoryPriority = categoryService.getLast(user, addRequest.getType())
					.map(Category::getPriority)
					.orElse(1);

			Category category = new Category();
			category.setName(addRequest.getName());
			category.setUser(user);
			category.setType(addRequest.getType());
			category.setPriority(lastCategoryPriority + 1);

			Category addedCategory = categoryService.save(category);

			return ResponseEntity.ok(addedCategory.toDTO());
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable long id) {
		Category category = categoryService.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));

		categoryService.delete(category.getId());
	}
}
