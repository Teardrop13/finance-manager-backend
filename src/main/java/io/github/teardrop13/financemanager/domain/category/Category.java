package io.github.teardrop13.financemanager.domain.category;

import io.github.teardrop13.authentication.user.User;
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
	@JoinColumn(name = "USER_ID")
	private User user;

	@Column(name = "PRIORITY")
	private Integer priority;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DELETED")
	private boolean deleted;

	public CategoryDTO toDTO() {
		return new CategoryDTO(id, priority, name);
	}
}
