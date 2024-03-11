package pl.teardrop.financemanager.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import pl.teardrop.authentication.user.service.DefaultUserService;
import pl.teardrop.authentication.user.domain.Email;
import pl.teardrop.authentication.user.domain.Password;
import pl.teardrop.authentication.user.service.PasswordEncryptor;
import pl.teardrop.authentication.user.domain.User;
import pl.teardrop.authentication.user.repository.UserRepository;
import pl.teardrop.authentication.user.service.UserService;
import pl.teardrop.financemanager.domain.category.service.AddingDefaultCategoriesService;

public class FinancialManagerUserService extends DefaultUserService implements UserService {

	private final AddingDefaultCategoriesService addingDefaultCategoriesService;

	@Autowired
	public FinancialManagerUserService(UserRepository userRepository,
									   PasswordEncryptor passwordEncryptor,
									   AddingDefaultCategoriesService addingDefaultCategoriesService) {
		super(userRepository, passwordEncryptor);
		this.addingDefaultCategoriesService = addingDefaultCategoriesService;
	}

	@Override
	public User create(Email email, Password password) {
		User userAdded = super.create(email, password);
		addingDefaultCategoriesService.add(userAdded.userId());
		return userAdded;
	}
}
