package pl.teardrop.financemanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.exceptions.UserNotFoundException;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.controller.exceptions.CategoryNotFoundException;
import pl.teardrop.financemanager.controller.exceptions.FinancialRecordNotFoundException;
import pl.teardrop.financemanager.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.model.FinancialRecord;
import pl.teardrop.financemanager.service.CategoryService;
import pl.teardrop.financemanager.service.FinancialRecordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
@Slf4j
public class FinancialRecordController {

	private final FinancialRecordService recordService;

	private final CategoryService categoryService;

	@GetMapping("/all")
	public List<FinancialRecordDTO> financialRecords() {
		return UserUtils.currentUser()
				.map(user -> recordService.getByUser(user).stream()
						.map(FinancialRecord::toDTO)
						.toList())
				.orElseThrow(() -> new UserNotFoundException("Could not retrieve user's FinancialRecords. User not found."));
	}

	@GetMapping("/records/{category}")
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
	public void addFinancialRecord(@RequestBody FinancialRecordDTO financialRecordDTO) {
		UserUtils.currentUser()
				.ifPresentOrElse(user -> {
									 FinancialRecord financialRecord = new FinancialRecord();
									 financialRecord.setUser(user);
									 financialRecord.setDescription(financialRecord.getDescription());
									 financialRecord.setAmount(financialRecord.getAmount());
									 categoryService.getByUserAndName(user, financialRecordDTO.getCategoryName()).ifPresentOrElse(
											 category -> financialRecord.setCategory(category),
											 () -> {
												 throw new CategoryNotFoundException("Failed to create FinancialRecord. Category " + financialRecordDTO.getCategoryName() + " not found for user id=" + user.getId());
											 }
									 );
									 recordService.save(financialRecord);
								 },
								 () -> {
									 throw new UserNotFoundException("Failed to create FinancialRecord. User not found.");
								 });
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
