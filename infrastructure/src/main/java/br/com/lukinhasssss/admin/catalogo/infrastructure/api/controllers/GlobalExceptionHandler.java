package br.com.lukinhasssss.admin.catalogo.infrastructure.api.controllers;

import br.com.lukinhasssss.admin.catalogo.domain.exceptions.DomainException;
import br.com.lukinhasssss.admin.catalogo.domain.validation.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DomainException.class)
    public ResponseEntity<?> handlerDomainException(final DomainException ex) {
        return ResponseEntity.unprocessableEntity().body(ApiError.from(ex));
    }

    record ApiError(List<Error> errors, String message) {
        static ApiError from(final DomainException ex) {
            return new ApiError(ex.getErrors(), ex.getMessage());
        }
    }
}
