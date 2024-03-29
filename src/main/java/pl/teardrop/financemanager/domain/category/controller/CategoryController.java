package pl.teardrop.financemanager.domain.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.user.domain.UserId;
import pl.teardrop.authentication.user.service.UserUtils;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryCommand;
import pl.teardrop.financemanager.domain.category.dto.AddCategoryRequest;
import pl.teardrop.financemanager.domain.category.dto.CategoryDTO;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoriesCommand;
import pl.teardrop.financemanager.domain.category.dto.ReorderCategoriesRequest;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoriesRequest;
import pl.teardrop.financemanager.domain.category.dto.UpdateCategoryCommand;
import pl.teardrop.financemanager.domain.category.exception.CategoryExistException;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.exception.InvalidReorderCategoryCommandException;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.service.AddingCategoryService;
import pl.teardrop.financemanager.domain.category.service.CategoriesReorderingService;
import pl.teardrop.financemanager.domain.category.service.CategoryDeletingService;
import pl.teardrop.financemanager.domain.category.service.CategoryMapper;
import pl.teardrop.financemanager.domain.category.service.CategoryRetrievingService;
import pl.teardrop.financemanager.domain.category.service.CategorySavingService;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

	private final CategoryRetrievingService categoryRetrievingService;
	private final CategoryDeletingService categoryDeletingService;
	private final CategoryMapper categoryMapper;
	private final CategoriesReorderingService categoriesReorderingService;
	private final CategorySavingService categorySavingService;
	private final AddingCategoryService addingCategoryService;

	@GetMapping("/{type}")
	public ResponseEntity<Object> getCategories(@PathVariable("type") FinancialRecordType type) {
		UserId userId = UserUtils.currentUserId();

		List<Category> categories = categoryRetrievingService.getNotDeletedByUserAndType(userId, type);
		List<CategoryDTO> categoriesDTO = categories.stream()
				.map(categoryMapper::toDTO)
				.toList();
		return ResponseEntity.ok(categoriesDTO);
	}

	@PutMapping
	public ResponseEntity<Object> update(@RequestBody UpdateCategoriesRequest request) {
		List<UpdateCategoryCommand> updateCommands = request.updateCategoryRequests().stream()
				.map(r -> new UpdateCategoryCommand(r))
				.toList();
		try {
			for (UpdateCategoryCommand command : updateCommands) {
				categorySavingService.update(command);
			}
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
		}
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{type}/reorder")
	public ResponseEntity<Object> reorderCategories(@PathVariable("type") FinancialRecordType type,
													@RequestBody ReorderCategoriesRequest request) {
		UserId userId = UserUtils.currentUserId();
		try {
			categoriesReorderingService.reorder(new ReorderCategoriesCommand(userId, type, request));
			return ResponseEntity.noContent().build();
		} catch (InvalidReorderCategoryCommandException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestBody AddCategoryRequest request) {
		UserId userId = UserUtils.currentUserId();

		AddCategoryCommand command = new AddCategoryCommand(userId, request);

		try {
			Category category = addingCategoryService.add(command);
			return ResponseEntity.ok(categoryMapper.toDTO(category));
		} catch (CategoryExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable long id) {
		Optional<Category> categoryOpt = categoryRetrievingService.getById(new CategoryId(id));

		if (categoryOpt.isPresent()) {
			categoryDeletingService.delete(categoryOpt.get().categoryId());
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category id=%s not found.".formatted(id));
		}
	}
}
