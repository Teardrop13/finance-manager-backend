package pl.teardrop.financemanager.domain.category.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class CategoryId {

	private Long id;

	public CategoryId(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null");
		}
		this.id = id;
	}
}
