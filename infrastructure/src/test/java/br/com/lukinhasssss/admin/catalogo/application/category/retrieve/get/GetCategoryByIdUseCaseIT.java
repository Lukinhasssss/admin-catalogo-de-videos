package br.com.lukinhasssss.admin.catalogo.application.category.retrieve.get;

import br.com.lukinhasssss.admin.catalogo.IntegrationTest;
import br.com.lukinhasssss.admin.catalogo.domain.category.Category;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryGateway;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryID;
import br.com.lukinhasssss.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
class GetCategoryByIdUseCaseIT {

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
            Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = aCategory.getId();

        save(aCategory);

        final var actualCategory = useCase.execute(expectedId.getValue());

        assertEquals(expectedId, actualCategory.id());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.createdAt());
        assertNotNull(actualCategory.updatedAt());
        assertNull(actualCategory.deletedAt());
//        assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
//        assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
//        assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsGetCategory_shouldReturnNotFound() {
        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedId = CategoryID.from("123");

        final var actualException = assertThrows(
            NotFoundException.class, () -> useCase.execute(expectedId.getValue())
        );

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    void givenAValidID_whenGatewayThrowsException_shouldReturnException() {
        final var expectedErrorMessage = "Gateway error";
        final var expectedId = CategoryID.from("123");

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(categoryGateway).findById(expectedId);

        final var actualException = assertThrows(
            IllegalStateException.class, () -> useCase.execute(expectedId.getValue())
        );

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(
            Arrays.stream(aCategory).map(CategoryJpaEntity::from).toList()
        );
    }
}
