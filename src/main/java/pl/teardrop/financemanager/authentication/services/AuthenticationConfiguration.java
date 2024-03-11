package pl.teardrop.financemanager.authentication.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import pl.teardrop.authentication.user.service.PasswordEncryptor;
import pl.teardrop.authentication.user.repository.UserRepository;
import pl.teardrop.authentication.user.service.UserService;
import pl.teardrop.financemanager.domain.category.service.AddingDefaultCategoriesService;

@Configuration
public class AuthenticationConfiguration {

	@Bean
	public UserService userService(UserRepository userRepository,
								   @Lazy PasswordEncryptor passwordEncryptor,
								   AddingDefaultCategoriesService addingDefaultCategoriesService) {
		return new FinancialManagerUserService(userRepository, passwordEncryptor, addingDefaultCategoriesService);
	}

}
