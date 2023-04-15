package pl.teardrop.financemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan({
		"pl.teardrop.authentication",
		"pl.teardrop.financemanager"
})
@EntityScan({
		"pl.teardrop.authentication.user",
		"pl.teardrop.financemanager.model"
})
@EnableJpaRepositories({
		"pl.teardrop.authentication.user",
		"pl.teardrop.financemanager.repository"
})
public class FinanceManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceManagerApplication.class, args);
	}

}
