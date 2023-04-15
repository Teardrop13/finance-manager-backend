package io.github.teardrop13.financemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan({
		"io.github.teardrop13.authentication",
		"io.github.teardrop13.financemanager"
})
@EntityScan({
		"io.github.teardrop13.authentication.user",
		"io.github.teardrop13.financemanager.domain"
})
@EnableJpaRepositories({
		"io.github.teardrop13.authentication.user",
		"io.github.teardrop13.financemanager.repositories"
})
public class FinanceManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceManagerApplication.class, args);
	}

}
