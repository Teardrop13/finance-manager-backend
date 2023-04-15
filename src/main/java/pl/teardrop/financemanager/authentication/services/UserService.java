package pl.teardrop.financemanager.authentication.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.service.CategoryService;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends pl.teardrop.authentication.user.UserService {

	private final CategoryService categoryService;

	@Override
	protected void runAfterUserCreation(User user) {
		categoryService.addDefaultCategorires(user);
	}
}
