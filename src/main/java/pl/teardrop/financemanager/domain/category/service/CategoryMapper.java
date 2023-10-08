package pl.teardrop.financemanager.domain.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.teardrop.financemanager.domain.category.dto.CategoryDTO;
import pl.teardrop.financemanager.domain.category.model.Category;

@Service
@Slf4j
public class CategoryMapper {

	public CategoryDTO toDTO(Category category) {
		return new CategoryDTO(category.getId(),
							   category.getPriority().getValue(),
							   category.getName());
	}
}
