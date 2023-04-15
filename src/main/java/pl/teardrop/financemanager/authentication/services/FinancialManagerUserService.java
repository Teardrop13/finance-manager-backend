package pl.teardrop.financemanager.authentication.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.DefaultUserService;
import pl.teardrop.authentication.user.User;
import pl.teardrop.authentication.user.UserService;
import pl.teardrop.financemanager.service.CategoryService;

@Primary
@Service("userService")
@Slf4j
@RequiredArgsConstructor
public class FinancialManagerUserService extends DefaultUserService implements UserService {

	private final CategoryService categoryService;

	@Override
	public User create(String username, String password, String email) {
		User userAdded = super.create(username, password, email);
		categoryService.addDefaultCategorires(userAdded);
		return userAdded;
	}
}
