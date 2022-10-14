package br.com.lukinhasssss.admin.catalogo.application.category.delete;

import br.com.lukinhasssss.admin.catalogo.IntegrationTest;
import br.com.lukinhasssss.admin.catalogo.domain.category.Category;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryGateway;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryID;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@IntegrationTest
class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsDeleteCategory_shouldBeOK() {
        final var aCategory =
            Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var expectedId = aCategory.getId();

        save(aCategory);

        assertEquals(1, categoryRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCategory_shouldBeOK() {
        final var expectedId = CategoryID.from("123");

        assertEquals(0, categoryRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var aCategory =
            Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var expectedId = aCategory.getId();

        doThrow(new IllegalStateException("Gateway error"))
            .when(categoryGateway).deleteById(eq(expectedId));

        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(eq(expectedId));
    }

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(
            Arrays.stream(aCategory).map(CategoryJpaEntity::from).toList()
        );
    }
}
