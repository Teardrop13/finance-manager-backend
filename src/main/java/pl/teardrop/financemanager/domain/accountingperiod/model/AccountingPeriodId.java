package pl.teardrop.financemanager.domain.accountingperiod.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class AccountingPeriodId implements Serializable {

	private Long id;

	public AccountingPeriodId(Long id) {
		this.id = Objects.requireNonNull(id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
