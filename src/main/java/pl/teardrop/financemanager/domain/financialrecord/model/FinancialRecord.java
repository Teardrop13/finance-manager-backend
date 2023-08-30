package pl.teardrop.financemanager.domain.financialrecord.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.accountingperiod.model.AccountingPeriodId;
import pl.teardrop.financemanager.domain.category.model.CategoryId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "FM_RECORD")
@NoArgsConstructor
@Builder()
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FinancialRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "USER_ID", nullable = false))
	private UserId userId;

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID", nullable = false))
	private CategoryId categoryId;

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "ACCOUNTING_PERIOD_ID", nullable = false))
	private AccountingPeriodId accountingPeriodId;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "AMOUNT", precision = 12, scale = 4, nullable = false)
	private BigDecimal amount;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private LocalDate transactionDate;

	@Column(name = "TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private FinancialRecordType type;

	public FinancialRecordId financialRecordId() {
		return new FinancialRecordId(getId());
	}
}
