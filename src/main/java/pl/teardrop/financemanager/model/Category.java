package pl.teardrop.financemanager.model;

import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.CategoryDTO;
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

	@Column(name = "PRIORITY", nullable = false)
	private Integer priority;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	public CategoryDTO toDTO() {
		return new CategoryDTO(id, priority, name);
	}
}
