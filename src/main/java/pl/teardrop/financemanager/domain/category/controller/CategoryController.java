package pl.teardrop.financemanager.domain.category.controller;

import lombok.AllArgsConstructor;
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
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryRequest;
import pl.teardrop.financemanager.domain.category.dto.CategoryDTO;
import pl.teardrop.financemanager.domain.category.exception.CategoryExistException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.CategoryMapper;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;

	@GetMapping("/{type}")
	public ResponseEntity<Object> getCategories(@PathVariable("type") String typeTextValue) {
		UserId userId = UserUtils.currentUserId();

		try {
			FinancialRecordType type = FinancialRecordType.getByTextValue(typeTextValue);
			List<Category> categories = categoryService.getNotDeletedByUserAndType(userId, type);
			List<CategoryDTO> categoriesDTO = categories.stream()
					.map(categoryMapper::toDTO)
					.toList();
			return ResponseEntity.ok(categoriesDTO);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping
	public ResponseEntity<Object> save(@RequestBody List<CategoryDTO> categories) {
		categories.forEach(updatedCategory -> {
			categoryService.getById(new CategoryId(updatedCategory.id()))
					.ifPresent(category -> {
						category.setName(updatedCategory.name());
						category.setPriority(updatedCategory.priority());
						categoryService.save(category);
					});
		});
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestBody AddCategoryRequest request) {
		UserId userId = UserUtils.currentUserId();

		AddCategoryCommand command = new AddCategoryCommand(userId, request);

		try {
			Category category = categoryService.create(command);
			return ResponseEntity.ok(categoryMapper.toDTO(category));
		} catch (CategoryExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> delete(@PathVariable long id) {
		Optional<Category> categoryOpt = categoryService.getById(new CategoryId(id));

		if (categoryOpt.isPresent()) {
			categoryService.delete(categoryOpt.get().categoryId());
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category id=%s not found.".formatted(id));
		}
	}
}
