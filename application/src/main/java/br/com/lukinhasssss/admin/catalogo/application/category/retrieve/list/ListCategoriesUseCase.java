package br.com.lukinhasssss.admin.catalogo.application.category.retrieve.list;

import br.com.lukinhasssss.admin.catalogo.application.UseCase;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategorySearchQuery;
import br.com.lukinhasssss.admin.catalogo.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase
    extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> { }
