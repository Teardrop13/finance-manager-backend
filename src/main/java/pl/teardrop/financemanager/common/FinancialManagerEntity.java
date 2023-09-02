package pl.teardrop.financemanager.common;

public interface FinancialManagerEntity {

	Long getId();

	default boolean isNew() {
		return getId() == null;
	}
}
