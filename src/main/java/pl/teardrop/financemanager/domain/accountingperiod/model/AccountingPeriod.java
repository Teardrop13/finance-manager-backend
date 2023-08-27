package pl.teardrop.financemanager.domain.accountingperiod.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.dto.AccountingPeriodDTO;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "FM_ACCOUNTING_PERIOD")
public class AccountingPeriod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "USER_ID", nullable = false))
	private UserId userId;

	@Column(name = "STARTS_ON", nullable = false)
	private LocalDate startsOn;

	@Column(name = "ENDS_ON", nullable = false)
	private LocalDate endsOn;

	public AccountingPeriodId accountingPeriodId() {
		return new AccountingPeriodId(getId());
	}

	public AccountingPeriodDTO toDTO() {
		return new AccountingPeriodDTO(id, startsOn, endsOn);
	}
}
