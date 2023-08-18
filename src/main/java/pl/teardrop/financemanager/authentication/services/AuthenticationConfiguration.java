package pl.teardrop.financemanager.authentication.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.teardrop.authentication.user.UserRepository;
import pl.teardrop.authentication.user.UserService;
import pl.teardrop.financemanager.service.CategoryService;

@Configuration
public class AuthenticationConfiguration {

	@Bean
	public UserService userService(UserRepository userRepository,
								   @Lazy PasswordEncoder passwordEncoder,
								   CategoryService categoryService) {
		return new FinancialManagerUserService(userRepository, passwordEncoder, categoryService);
	}

}
