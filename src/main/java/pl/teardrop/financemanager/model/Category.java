package pl.teardrop.financemanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.CategoryDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
