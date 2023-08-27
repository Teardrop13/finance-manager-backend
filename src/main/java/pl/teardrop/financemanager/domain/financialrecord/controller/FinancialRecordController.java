package pl.teardrop.financemanager.domain.financialrecord.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.domain.financialrecord.dto.UpdateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.exception.FinancialRecordNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;
import pl.teardrop.financemanager.domain.financialrecord.service.FinancialRecordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
@Slf4j
public class FinancialRecordController {

	private final FinancialRecordService recordService;
	private final CategoryService categoryService;

	@GetMapping
	public List<FinancialRecordDTO> get(@RequestParam long periodId,
										@RequestParam FinancialRecordType type,
										@RequestParam int page,
										@RequestParam int pageSize,
										@RequestParam String sortBy,
										@RequestParam Boolean isAscending) {
		UserId userId = UserUtils.currentUserId();
		return recordService.getByUser(userId,
									   new AccountingPeriodId(periodId),
									   type,
									   page,
									   pageSize,
									   sortBy != null ? sortBy : "transactionDate",
									   isAscending != null ? isAscending : false).stream()
				.map(recordService::getDto)
				.toList();
	}

	@GetMapping("/all")
	public List<FinancialRecordDTO> getAll(@RequestParam int page,
										   @RequestParam int pageSize) {
		UserId userId = UserUtils.currentUserId();
		return recordService.getByUser(userId, page, pageSize).stream()
				.map(recordService::getDto)
				.toList();
	}

	@GetMapping("/count")
	public Integer getCount(@RequestParam long periodId,
							@RequestParam FinancialRecordType type) {
		UserId userId = UserUtils.currentUserId();
		return recordService.getRecordsCount(userId, new AccountingPeriodId(periodId), type);
	}

	@GetMapping("/count/all")
	public Integer getCount() {
		UserId userId = UserUtils.currentUserId();
		return recordService.getRecordsCount(userId);
	}

	@GetMapping("/category/{category}")
	public List<FinancialRecordDTO> getByCategory(@PathVariable(value = "category") String categoryText) {
		UserId userId = UserUtils.currentUserId();
		return categoryService.getByUser(userId).stream()
				.filter(c -> c.getName().equals(categoryText))
				.findFirst()
				.map(category -> recordService.getByCategory(category.categoryId()).stream()
						.map(recordService::getDto)
						.toList())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not retrieve user's FinancialRecords. Category not found: " + categoryText));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FinancialRecordDTO add(@Valid @RequestBody CreateFinancialRecordCommand createCommand) {
		UserId userId = UserUtils.currentUserId();

		try {
			FinancialRecord recordAdded = recordService.create(userId, createCommand);
			return recordService.getDto(recordAdded);
		} catch (CategoryNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category \"%s\" not found".formatted(createCommand.getCategory()));
		}
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public void update(@Valid @RequestBody UpdateFinancialRecordCommand updateCommand) {
		try {
			recordService.update(updateCommand);
		} catch (FinancialRecordNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FinancialRecord not found.");
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable long id) {
		try {
			recordService.delete(new FinancialRecordId(id));
		} catch (FinancialRecordNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FinancialRecord not found.");
		}
	}

}
