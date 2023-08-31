package pl.teardrop.financemanager.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({
		"pl.teardrop.financemanager",
		"pl.teardrop.authentication",
})
@EntityScan({
		"pl.teardrop.financemanager",
		"pl.teardrop.authentication",
})
@EnableJpaRepositories({
		"pl.teardrop.financemanager",
		"pl.teardrop.authentication",
})
public class ScanConfiguration {

}
