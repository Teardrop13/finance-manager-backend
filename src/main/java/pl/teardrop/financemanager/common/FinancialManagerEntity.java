package pl.teardrop.financemanager.common;

import pl.teardrop.authentication.user.domain.UserId;

public interface FinancialManagerEntity {

	Long getId();

	UserId getUserId();

	default boolean isNew() {
		return getId() == null;
	}
}
