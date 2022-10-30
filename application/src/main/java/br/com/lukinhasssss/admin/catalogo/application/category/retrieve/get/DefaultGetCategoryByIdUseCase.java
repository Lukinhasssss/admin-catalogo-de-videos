package br.com.lukinhasssss.admin.catalogo.application.category.retrieve.get;

import br.com.lukinhasssss.admin.catalogo.domain.category.Category;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryGateway;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryID;
import br.com.lukinhasssss.admin.catalogo.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CategoryOutput execute(String anIn) {
        final var anCategoryID = CategoryID.from(anIn);

        return this.categoryGateway.findById(anCategoryID)
            .map(CategoryOutput::from)
            .orElseThrow(notFound(anCategoryID));
    }

    private static Supplier<NotFoundException> notFound(final CategoryID anId) {
        return () -> NotFoundException.with(Category.class, anId);
    }
}
