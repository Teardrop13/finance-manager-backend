package pl.teardrop.financemanager.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class MethodSecurityConfiguration {

	private final CustomPermissionEvaluator customPermissionEvaluator;

	@Bean
	public MethodSecurityExpressionHandler expressionHandler() {
		var expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
		return expressionHandler;
	}
}
