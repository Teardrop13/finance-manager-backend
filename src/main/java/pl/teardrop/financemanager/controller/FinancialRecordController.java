package pl.teardrop.financemanager.controller;

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
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.dto.UpdateFinancialRecordCommand;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.service.CategoryService;
import pl.teardrop.financemanager.service.FinancialRecordService;
import pl.teardrop.financemanager.service.exceptions.CategoryNotFoundException;
import pl.teardrop.financemanager.service.exceptions.FinancialRecordNotFoundException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
@Slf4j
public class FinancialRecordController {

	private final FinancialRecordService recordService;
	private final CategoryService categoryService;

	@GetMapping
	public List<FinancialRecordDTO> get(@RequestParam int periodId,
										@RequestParam FinancialRecordType type,
										@RequestParam int page,
										@RequestParam int pageSize,
										@RequestParam String sortBy,
										@RequestParam Boolean isAscending) {
		return UserUtils.currentUser()
				.map(user -> recordService.getByUser(user,
													 periodId,
													 type,
													 page,
													 pageSize,
													 sortBy != null ? sortBy : "transactionDate",
													 isAscending != null ? isAscending : false).stream()
						.map(FinancialRecord::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's FinancialRecords. User not found."));
	}

	@GetMapping("/all")
	public List<FinancialRecordDTO> getAll(@RequestParam int page,
										   @RequestParam int pageSize) {
		return UserUtils.currentUser()
				.map(user -> recordService.getByUser(user, page, pageSize).stream()
						.map(FinancialRecord::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's FinancialRecords. User not found."));
	}

	@GetMapping("/count")
	public Integer getCount(@RequestParam int periodId,
							@RequestParam FinancialRecordType type) {
		return UserUtils.currentUser()
				.map(user -> recordService.getRecordsCount(user, periodId, type))
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's FinancialRecords' count. User not found."));
	}

	@GetMapping("/count/all")
	public Integer getCount() {
		return UserUtils.currentUser()
				.map(user -> recordService.getRecordsCount(user))
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's FinancialRecords' count. User not found."));
	}

	@GetMapping("/category/{category}")
	public List<FinancialRecordDTO> getByCategory(@PathVariable(value = "category") String categoryText) {
		return UserUtils.currentUser()
				.map(categoryService::getByUser)
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's Categories. User not found."))
				.stream()
				.filter(c -> c.getName().equals(categoryText))
				.findFirst()
				.map(category -> recordService.getByCategory(category).stream()
						.map(FinancialRecord::toDTO)
						.toList())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not retrieve user's FinancialRecords. Category not found: " + categoryText));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FinancialRecordDTO add(@Valid @RequestBody CreateFinancialRecordCommand createCommand) {
		User user = UserUtils.currentUser()
				.orElseThrow(() -> new UserNotFoundException("Failed to create FinancialRecord. User not found."));

		try {
			FinancialRecord recordAdded = recordService.create(user, createCommand);
			return recordAdded.toDTO();
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
			recordService.delete(id);
		} catch (FinancialRecordNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FinancialRecord not found.");
		}
	}

}
