package pl.teardrop.financemanager.authentication.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.teardrop.authentication.user.UserRepository;
import pl.teardrop.authentication.user.UserService;
import pl.teardrop.financemanager.domain.category.service.CategoryService;
import pl.teardrop.financemanager.domain.category.service.DefaultCategoriesService;

@Configuration
public class AuthenticationConfiguration {

	@Bean
	public UserService userService(UserRepository userRepository,
								   @Lazy PasswordEncoder passwordEncoder,
								   DefaultCategoriesService defaultCategoriesService) {
		return new FinancialManagerUserService(userRepository, passwordEncoder, defaultCategoriesService);
	}

}
