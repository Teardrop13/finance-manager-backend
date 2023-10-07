package pl.teardrop.financemanager.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.teardrop.authentication.user.domain.User;
import pl.teardrop.financemanager.common.FinancialManagerEntity;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriod;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.accountingperiod.repository.AccountingPeriodRepository;
import pl.teardrop.financemanager.domain.category.model.Category;
import pl.teardrop.financemanager.domain.category.model.CategoryId;
import pl.teardrop.financemanager.domain.category.repository.CategoryRepository;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecord;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordId;
import pl.teardrop.financemanager.domain.financialrecord.repository.FinancialRecordRepository;

import java.io.Serializable;
import java.util.Objects;

@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

	@Lazy
	@Autowired
	private FinancialRecordRepository financialRecordRepository;
	@Lazy
	@Autowired
	private CategoryRepository categoryRepository;
	@Lazy
	@Autowired
	private AccountingPeriodRepository accountingPeriodRepository;

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		log.info("PermissionEvaluator - targetDomainObject={}", targetDomainObject);

		if (targetDomainObject instanceof FinancialManagerEntity entity
			&& authentication.getPrincipal() instanceof User user) {
			return Objects.equals(entity.getUserId(), user.userId());
		} else {
			log.error("Object {} is not instance of FinancialManagerEntity", targetDomainObject);
			return false;
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
			log.error("Wrong invocation of PermissionEvaluator - targetId={}, targetType={}, permission={}", targetId, targetType, permission);
			return false;
		}

		User user = (User) authentication.getPrincipal();

		FinancialManagerEntity entity;
		if (targetId instanceof FinancialRecordId id && FinancialRecord.class.getSimpleName().equals(targetType)) {
			entity = financialRecordRepository.findById(id.getId()).orElse(null);
		} else if (targetId instanceof AccountingPeriodId id && AccountingPeriod.class.getSimpleName().equals(targetType)) {
			entity = accountingPeriodRepository.findById(id.getId()).orElse(null);
		} else if (targetId instanceof CategoryId id && Category.class.getSimpleName().equals(targetType)) {
			entity = categoryRepository.findById(id.getId()).orElse(null);
		} else {
			log.error("Type of targedId does not match targetType - targedId={}, targetType={}", targetId != null ? targetId.getClass().getSimpleName() : null, targetType);
			return false;
		}

		boolean result = entity != null && Objects.equals(entity.getUserId(), user.userId());
		log.info("Verifying permission for userId={} to {} {} with id {}, result={}", user.getId(), permission, targetType, targetId, result);

		return result;
	}
}
