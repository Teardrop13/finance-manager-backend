package pl.teardrop.financemanager.domain.category.controller;

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
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.dto.CategoryDTO;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/income")
	public List<CategoryDTO> getIncomeCategories() {
		UserId userId = UserUtils.currentUserId();
		return categoryService.getByUserAndType(userId, FinancialRecordType.INCOME).stream()
				.filter(category -> !category.isDeleted())
				.map(Category::toDTO)
				.toList();
	}

	@GetMapping("/expense")
	public List<CategoryDTO> getExpenseCategories() {
		UserId userId = UserUtils.currentUserId();
		return categoryService.getByUserAndType(userId, FinancialRecordType.EXPENSE).stream()
				.filter(category -> !category.isDeleted())
				.map(Category::toDTO)
				.toList();
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public void save(@RequestBody List<CategoryDTO> categories) {
		categories.forEach(updatedCategory -> {
			categoryService.getById(new CategoryId(updatedCategory.getId()))
					.ifPresent(category -> {
						category.setName(updatedCategory.getName());
						category.setPriority(updatedCategory.getPriority());
						categoryService.save(category);
					});
		});
	}

	@PostMapping
	public ResponseEntity<CategoryDTO> add(@RequestBody AddCategoryCommand command) {
		UserId userId = UserUtils.currentUserId();

		Optional<Category> categoryOptional = categoryService.getByUserAndTypeAndName(userId, command.getType(), command.getName());
		if (categoryOptional.isPresent()) {
			Category existingCategory = categoryOptional.get();
			log.info("Category with name \"{}\" exists", command.getName());

			if (existingCategory.isDeleted()) {
				existingCategory.setName(command.getName());
				existingCategory.setDeleted(false);
				categoryService.getLast(existingCategory.getUserId(), existingCategory.getType()).ifPresent(last -> {
					existingCategory.setPriority(last.getPriority() + 1);
				});
				Category existingCategoryUpdated = categoryService.save(existingCategory);
				categoryService.reorder(userId, command.getType());
				return ResponseEntity.ok(existingCategoryUpdated.toDTO());
			} else {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with name \"" + command.getName() + "\" exists");
			}
		} else {
			int lastCategoryPriority = categoryService.getLast(userId, command.getType())
					.map(Category::getPriority)
					.orElse(1);

			Category category = new Category();
			category.setName(command.getName());
			category.setUserId(userId);
			category.setType(command.getType());
			category.setPriority(lastCategoryPriority + 1);

			Category addedCategory = categoryService.save(category);

			return ResponseEntity.ok(addedCategory.toDTO());
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable long id) {
		Category category = categoryService.getById(new CategoryId(id))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));

		categoryService.delete(category.categoryId());
	}
}
