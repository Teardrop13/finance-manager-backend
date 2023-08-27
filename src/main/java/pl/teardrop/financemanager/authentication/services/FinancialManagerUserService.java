package pl.teardrop.financemanager.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.teardrop.authentication.user.DefaultUserService;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserRepository;
import pl.teardrop.authentication.user.UserService;
import pl.teardrop.financemanager.domain.category.service.CategoryService;

public class FinancialManagerUserService extends DefaultUserService implements UserService {

	private final CategoryService categoryService;

	@Autowired
	public FinancialManagerUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CategoryService categoryService) {
		super(userRepository, passwordEncoder);
		this.categoryService = categoryService;
	}

	@Override
	public User create(String username, String password, String email) {
		User userAdded = super.create(username, password, email);
		categoryService.addDefaultCategorires(userAdded);
		return userAdded;
	}
}
