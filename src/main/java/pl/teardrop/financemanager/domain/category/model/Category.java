package pl.teardrop.financemanager.domain.category.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.domain.category.dto.CategoryDTO;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

@Entity
@Getter
@Setter
@Table(name = "FM_CATEGORY")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@JsonProperty("priority")
	@Column(name = "PRIORITY", nullable = false)
	private Integer priority;

	@JsonProperty("name")
	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	@JsonProperty("type")
	@Column(name = "TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private FinancialRecordType type;

	public CategoryDTO toDTO() {
		return new CategoryDTO(id, priority, name);
	}
}
