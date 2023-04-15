package pl.teardrop.financemanager.model;

import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.FinancialRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "FM_RECORD")
public class FinancialRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PERIOD_ID", nullable = false)
	private Period period;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "AMOUNT", precision = 12, scale = 4, nullable = false)
	private BigDecimal amount;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private LocalDate transactionDate;

	public FinancialRecordDTO toDTO() {
		return new FinancialRecordDTO(id, description, amount);
	}
}
