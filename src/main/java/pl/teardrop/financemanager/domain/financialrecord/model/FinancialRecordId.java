package pl.teardrop.financemanager.domain.financialrecord.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class FinancialRecordId {

	private Long id;

	public FinancialRecordId(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null");
		}
		this.id = id;
	}
}
