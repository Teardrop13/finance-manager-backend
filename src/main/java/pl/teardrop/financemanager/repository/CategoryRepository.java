package pl.teardrop.financemanager.repository;

import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Category;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CategoryRepository extends Repository<Category, Long> {

	Optional<Category> findById(Long id);

	List<Category> findAll();

	List<Category> findByUserOrderByPriority(User user);

	Category save(Category category);

	void deleteById(Long id);

}