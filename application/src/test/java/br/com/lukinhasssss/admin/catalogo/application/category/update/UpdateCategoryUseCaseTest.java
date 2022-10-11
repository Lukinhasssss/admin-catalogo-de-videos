package br.com.lukinhasssss.admin.catalogo.application.category.update;

import br.com.lukinhasssss.admin.catalogo.domain.category.Category;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryGateway;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryID;
import br.com.lukinhasssss.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var aCategory = Category.newCategory("Filme", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(), expectedName, expectedDescription, expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(aCategory.clone()));

        when(categoryGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(
            argThat(anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
                && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
                && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
                && Objects.equals(expectedId, anUpdatedCategory.getId())
                && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
                && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
                && Objects.isNull(anUpdatedCategory.getDeletedAt()))
        );
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory("Filmes", null, true);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(), expectedName, expectedDescription, expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(aCategory.clone()));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory("Filme", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(aCategory.clone()));

        when(categoryGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        final var actualOutput = useCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));

        verify(categoryGateway, times(1)).update(argThat(
            anUpdatedCategory ->
                Objects.equals(expectedName, anUpdatedCategory.getName())
                    && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
                    && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
                    && Objects.equals(expectedId, anUpdatedCategory.getId())
                    && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
                    && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
                    && Objects.nonNull(anUpdatedCategory.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAnException() {
        final var aCategory = Category.newCategory("Filme", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Gateway error";

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(), expectedName, expectedDescription, expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(aCategory.clone()));

        when(categoryGateway.update(any()))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).update(argThat(
            anUpdatedCategory ->
                Objects.equals(expectedName, anUpdatedCategory.getName())
                    && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
                    && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
                    && Objects.equals(expectedId, anUpdatedCategory.getId())
                    && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
                    && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
                    && Objects.isNull(anUpdatedCategory.getDeletedAt())
        ));
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateCategory_shouldReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = "123";
        final var expectedErrorMessage = "Category with ID 123 was not found";

        final var aCommand = UpdateCategoryCommand.with(
            expectedId, expectedName, expectedDescription, expectedIsActive
        );

        when(categoryGateway.findById(eq(CategoryID.from(expectedId))))
            .thenReturn(Optional.empty());

        final var actualException =
            assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(categoryGateway, times(1)).findById(eq(CategoryID.from(expectedId)));
        verify(categoryGateway, times(0)).update(any());
    }
}
