package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.controller.exceptions.FinancialRecordNotFoundException;
import pl.teardrop.financemanager.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.model.FinancialRecordType;
import pl.teardrop.financemanager.service.CategoryService;
import pl.teardrop.financemanager.service.FinancialRecordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
@Slf4j
public class FinancialRecordController {

	private final FinancialRecordService recordService;

	private final CategoryService categoryService;

	@GetMapping("/all")
	public List<FinancialRecordDTO> financialRecords(@RequestParam(value = "page") int page, @RequestParam(value = "page-size") int pageSize) {
		return UserUtils.currentUser()
				.map(user -> recordService.getByUser(user, page, pageSize).stream()
						.map(FinancialRecord::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's FinancialRecords. User not found."));
	}

	@GetMapping("category/{category}")
	public List<FinancialRecordDTO> financialRecords(@PathVariable(value = "category") String categoryText) {
		return UserUtils.currentUser()
				.map(categoryService::getByUser)
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's Categories. User not found."))
				.stream()
				.filter(c -> c.getName().equals(categoryText))
				.findFirst()
				.map(category -> recordService.getByCategory(category).stream()
						.map(FinancialRecord::toDTO)
						.toList())
				.orElseThrow(() -> new FinancialRecordNotFoundException("Could not retrieve user's FinancialRecords. Category not found: " + categoryText));
	}

	@PostMapping("/add")
	@ResponseStatus(HttpStatus.CREATED)
	public FinancialRecordDTO addFinancialRecord(@RequestBody FinancialRecordDTO financialRecordDTO) {
		User user = UserUtils.currentUser()
				.orElseThrow(() -> new UserNotFoundException("Failed to create FinancialRecord. User not found."));

		FinancialRecord financialRecord = new FinancialRecord();
		financialRecord.setUser(user);
		financialRecord.setDescription(financialRecordDTO.getDescription());
		financialRecord.setAmount(financialRecordDTO.getAmount());
		financialRecord.setTransactionDate(financialRecordDTO.getTransactionDate());
		FinancialRecordType.getByName(financialRecordDTO.getType()).ifPresentOrElse(
				financialRecord::setType,
				() -> {
					throw new RuntimeException("Type not found for string: " + financialRecordDTO.getType());
				});
		categoryService.getByUserAndName(user, financialRecordDTO.getCategory()).ifPresentOrElse(
				financialRecord::setCategory,
				() -> {
					throw new RuntimeException("Failed to create FinancialRecord. Category " + financialRecordDTO.getCategory() + " not found for user id=" + user.getId());
				}
		);
		FinancialRecord recordAdded = recordService.save(financialRecord);
		return recordAdded.toDTO();
	}

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
	public void saveFinancialRecord(@RequestBody FinancialRecordDTO financialRecordDTO) {
		recordService.getById(financialRecordDTO.getId())
				.ifPresentOrElse(financialRecord -> {
									 financialRecord.setDescription(financialRecordDTO.getDescription());
									 financialRecord.setAmount(financialRecordDTO.getAmount());
									 recordService.save(financialRecord);
								 },
								 () -> {
									 throw new FinancialRecordNotFoundException("FinancialRecord not found.");
								 });
	}

	@PostMapping("/delete")
	@ResponseStatus(HttpStatus.OK)
	public void deleteFinancialRecord(@RequestBody FinancialRecordDTO financialRecordDTO) {
		recordService.getById(financialRecordDTO.getId())
				.ifPresentOrElse(recordService::delete,
								 () -> {
									 throw new FinancialRecordNotFoundException("FinancialRecord not found.");
								 });
	}

}
