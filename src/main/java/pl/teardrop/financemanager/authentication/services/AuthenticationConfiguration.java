package pl.teardrop.financemanager.authentication.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.teardrop.authentication.user.UserRepository;
import pl.teardrop.authentication.user.UserService;
import pl.teardrop.financemanager.service.CategoryService;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfiguration {

	private final UserRepository userRepository;
	@Lazy
	private final PasswordEncoder passwordEncoder;
	private final CategoryService categoryService;

	@Bean
	public UserService userService() {
		return new FinancialManagerUserService(userRepository, passwordEncoder, categoryService);
	}

}
