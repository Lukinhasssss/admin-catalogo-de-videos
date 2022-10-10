package br.com.lukinhasssss.admin.catalogo.application.category.create;

import br.com.lukinhasssss.admin.catalogo.application.UseCase;
import br.com.lukinhasssss.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase
    extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {
}
