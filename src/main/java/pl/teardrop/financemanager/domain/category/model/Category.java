package pl.teardrop.financemanager.domain.category.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.teardrop.authentication.user.UserId;
import pl.teardrop.financemanager.domain.category.dto.CategoryDTO;
import pl.teardrop.financemanager.domain.financialrecord.model.FinancialRecordType;

@Entity
@Getter
@Setter
@Table(name = "FM_CATEGORY")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "USER_ID", nullable = false))
	private UserId userId;

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

	public CategoryId categoryId() {
		return new CategoryId(getId());
	}
}
