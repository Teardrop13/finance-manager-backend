package io.github.teardrop13.financemanager.domain.record;

import io.github.teardrop13.authentication.user.User;
import io.github.teardrop13.financemanager.domain.category.Category;
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
	@JoinColumn(name = "USER_ID")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PERIOD_ID", nullable = false)
	private Period period;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "AMOUNT", precision = 12, scale = 4)
	private BigDecimal amount;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	public FinancialRecordDTO toDTO() {
		return new FinancialRecordDTO(id, description, amount);
	}
}
