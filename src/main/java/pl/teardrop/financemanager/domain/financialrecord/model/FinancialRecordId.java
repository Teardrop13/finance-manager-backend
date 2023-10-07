package pl.teardrop.financemanager.domain.financialrecord.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class FinancialRecordId {

	private Long id;

	public FinancialRecordId(Long id) {
		this.id = Objects.requireNonNull(id);
	}
}
