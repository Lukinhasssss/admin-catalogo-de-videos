package br.com.lukinhasssss.admin.catalogo.application.category.update;

import br.com.lukinhasssss.admin.catalogo.application.UseCase;
import br.com.lukinhasssss.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase
    extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> { }
