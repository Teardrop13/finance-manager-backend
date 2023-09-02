package pl.teardrop.financemanager.domain.financialrecord.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordId;
import pl.teardrop.financemanager.domain.financialrecord.repository.FinancialRecordRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class FinancialRecordAccessTest {

	private final FinancialRecordRepository financialRecordRepository;

	public boolean test(@NonNull FinancialRecordId financialRecordId, Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		UserId userId = financialRecordRepository.findById(financialRecordId.getId())
				.map(FinancialRecord::getUserId)
				.orElse(null);

		boolean result = Objects.equals(user.userId(), userId);

		log.info("Testing if financialRecordId={} belongs to userid={}, result={}", financialRecordId.getId(), user.getId(), result);

		return result;
	}

}
