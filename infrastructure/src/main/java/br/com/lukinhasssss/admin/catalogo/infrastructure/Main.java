package br.com.lukinhasssss.admin.catalogo.infrastructure;

import br.com.lukinhasssss.admin.catalogo.domain.category.Category;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import br.com.lukinhasssss.admin.catalogo.infrastructure.configuration.WebServerConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "development");
        SpringApplication.run(WebServerConfig.class, args);
    }

    @Bean
    public ApplicationRunner runner(CategoryRepository repository) {
        return args -> {
            var all = repository.findAll();

            var filmes = Category.newCategory("Filmes", null, true);

            repository.saveAndFlush(CategoryJpaEntity.from(filmes));

            repository.deleteAll();
        };
    }
}