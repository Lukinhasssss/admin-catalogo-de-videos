package br.com.lukinhasssss.admin.catalogo.infrastructure.api.controllers;

import br.com.lukinhasssss.admin.catalogo.application.category.create.CreateCategoryCommand;
import br.com.lukinhasssss.admin.catalogo.application.category.create.CreateCategoryOutput;
import br.com.lukinhasssss.admin.catalogo.application.category.create.CreateCategoryUseCase;
import br.com.lukinhasssss.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import br.com.lukinhasssss.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import br.com.lukinhasssss.admin.catalogo.application.category.update.UpdateCategoryCommand;
import br.com.lukinhasssss.admin.catalogo.application.category.update.UpdateCategoryOutput;
import br.com.lukinhasssss.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import br.com.lukinhasssss.admin.catalogo.domain.pagination.Pagination;
import br.com.lukinhasssss.admin.catalogo.domain.validation.handler.Notification;
import br.com.lukinhasssss.admin.catalogo.infrastructure.api.CategoryAPI;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.models.CategoryApiOutput;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.models.CreateCategoryApiInput;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.models.UpdateCategoryApiInput;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.presenters.CategoryApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
        CreateCategoryUseCase createCategoryUseCase,
        GetCategoryByIdUseCase getCategoryByIdUseCase,
        UpdateCategoryUseCase updateCategoryUseCase,
        DeleteCategoryUseCase deleteCategoryUseCase
    ) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryApiInput input) {
        final var aCommand = CreateCategoryCommand.with(
            input.name(),
            input.description(),
            input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
            ResponseEntity.unprocessableEntity().body(notification);

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output ->
            ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);

        return createCategoryUseCase.execute(aCommand).fold(onError, onSuccess);
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

    @Override
    public CategoryApiOutput getById(final String id) {
        var output = getCategoryByIdUseCase.execute(id);
        return CategoryApiPresenter.present(output);
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateCategoryApiInput input) {
        final var aCommand = UpdateCategoryCommand.with(
            id,
            input.name(),
            input.description(),
            input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
            ResponseEntity.unprocessableEntity().body(notification);

        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess = ResponseEntity::ok;

        return updateCategoryUseCase.execute(aCommand).fold(onError, onSuccess);
    }

    @Override
    public void deleteById(final String id) {
        deleteCategoryUseCase.execute(id);
    }
}
