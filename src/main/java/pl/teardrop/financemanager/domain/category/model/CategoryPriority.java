package pl.teardrop.financemanager.domain.category.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class CategoryPriority implements Serializable, Comparable<CategoryPriority> {

	public static final CategoryPriority ONE = new CategoryPriority(1);

	private Integer value;

	public CategoryPriority(Integer value) {
		this.value = Objects.requireNonNull(value);
	}

	public CategoryPriority incremented() {
		return new CategoryPriority(getValue() + 1);
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	@Override
	public int compareTo(CategoryPriority other) {
		return Integer.compare(getValue(), other.getValue());
	}
}
