package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.dto.CategoryDTO;
import pl.teardrop.financemanager.model.Category;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.service.CategoryService;
import pl.teardrop.financemanager.service.FinancialRecordService;

import java.util.List;

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
						.map(Category::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's income categories. User not found."));
	}

	@GetMapping("/expense")
	public List<CategoryDTO> getExpenseCategories() {
		return UserUtils.currentUser()
				.map(user -> categoryService.getByUser(user, FinancialRecordType.EXPENSE).stream()
						.map(Category::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's expense categories. User not found."));
	}

}
