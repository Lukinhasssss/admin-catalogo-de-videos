package br.com.lukinhasssss.admin.catalogo.infrastructure.category;

import br.com.lukinhasssss.admin.catalogo.infrastructure.MySQLGatewayTest;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testInjectedDependencies() {
        assertNotNull(categoryGateway);
        assertNotNull(categoryRepository);
    }
}
