package br.com.lukinhasssss.admin.catalogo.infrastructure.api.controllers;

import br.com.lukinhasssss.admin.catalogo.domain.pagination.Pagination;
import br.com.lukinhasssss.admin.catalogo.infrastructure.api.CategoryAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryAPI {

    @Override
    public ResponseEntity<?> createCategory() {
        return null;
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        return null;
    }
}
