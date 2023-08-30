package pl.teardrop.financemanager.domain.financialrecord.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.authentication.user.UserUtils;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.category.exception.CategoryNotFoundException;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.dto.CreateFinancialRecordRequest;
import pl.teardrop.financemanager.domain.financialrecord.dto.FinancialRecordDTO;
import pl.teardrop.financemanager.domain.financialrecord.dto.FinancialRecordsHistoryDto;
import pl.teardrop.financemanager.domain.financialrecord.dto.UpdateFinancialRecordCommand;
import pl.teardrop.financemanager.domain.financialrecord.dto.UpdateFinancialRecordRequest;
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

	@GetMapping
	public ResponseEntity<Object> getPage(@RequestParam long periodId,
										  @RequestParam int page,
										  @RequestParam int pageSize,
										  @RequestParam FinancialRecordType type,
										  @RequestParam String sortBy,
										  @RequestParam Boolean isAscending) {
		UserId userId = UserUtils.currentUserId();
		AccountingPeriodId accountingPeriodId = new AccountingPeriodId(periodId);

		if (!List.of("amount", "transactionDate", "description").contains(sortBy)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorting by %s is not possible".formatted(sortBy));
		}

		int count = recordService.getRecordsCount(userId, accountingPeriodId, type);

		List<FinancialRecordDTO> records = recordService.getPage(userId,
																 accountingPeriodId,
																 type,
																 page,
																 pageSize,
																 sortBy != null ? sortBy : "transactionDate",
																 isAscending != null ? isAscending : false).stream()
				.map(recordService::getDto)
				.toList();

		return ResponseEntity.ok(new FinancialRecordsHistoryDto(count, records));
	}

	@PostMapping
	public ResponseEntity<Object> createRecord(@Valid @RequestBody CreateFinancialRecordRequest request) {
		UserId userId = UserUtils.currentUserId();

		try {
			CreateFinancialRecordCommand command = new CreateFinancialRecordCommand(userId, request);
			FinancialRecord recordAdded = recordService.create(command);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(recordService.getDto(recordAdded));
		} catch (CategoryNotFoundException e) {
			log.error("Failed to create new FinancialRecord", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category \"%s\" not found".formatted(request.getCategory()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateRecord(@PathVariable long id,
											   @Valid @RequestBody UpdateFinancialRecordRequest request) {
		try {
			UpdateFinancialRecordCommand command = new UpdateFinancialRecordCommand(new FinancialRecordId(id), request);
			FinancialRecord recordUpdated = recordService.update(command);
			return ResponseEntity.ok().body(recordService.getDto(recordUpdated));
		} catch (FinancialRecordNotFoundException e) {
			log.error("Failed to update FinancialRecord", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found.");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteRecord(@PathVariable long id) {
		try {
			recordService.delete(new FinancialRecordId(id));
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (FinancialRecordNotFoundException e) {
			log.error("Failed to delete FinancialRecord", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FinancialRecord not found.");
		}
	}
}