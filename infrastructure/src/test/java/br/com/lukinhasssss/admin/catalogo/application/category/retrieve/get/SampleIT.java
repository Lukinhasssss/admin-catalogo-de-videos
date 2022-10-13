package br.com.lukinhasssss.admin.catalogo.application.category.retrieve.get;

import br.com.lukinhasssss.admin.catalogo.IntegrationTest;
import br.com.lukinhasssss.admin.catalogo.application.category.create.CreateCategoryUseCase;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class SampleIT {

    @Autowired
    private CreateCategoryUseCase categoryUseCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testInjects() {
        assertNotNull(categoryUseCase);
        assertNotNull(categoryRepository);
    }
}
