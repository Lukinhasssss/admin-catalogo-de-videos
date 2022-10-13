package br.com.lukinhasssss.admin.catalogo.infrastructure.category;

import br.com.lukinhasssss.admin.catalogo.domain.category.Category;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryGateway;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryID;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategorySearchQuery;
import br.com.lukinhasssss.admin.catalogo.domain.pagination.Pagination;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import br.com.lukinhasssss.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category aCategory) {
        return save(aCategory);
    }

    @Override
    public void deleteById(final CategoryID anId) {
        final var AnIdValue = anId.getValue();

        if (categoryRepository.existsById(AnIdValue))
            categoryRepository.deleteById(AnIdValue);
    }

    @Override
    public Optional<Category> findById(final CategoryID anId) {
        return categoryRepository.findById(anId.getValue())
            .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category aCategory) {
        return save(aCategory);
    }

    /**
     * Paginação
     * Busca dinãmica pelo critério terms (name ou description)
     */
    @Override
    public Pagination<Category> findAll(CategorySearchQuery aQuery) {
        final var page = PageRequest.of(
            aQuery.page(),
            aQuery.perPage(),
            Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var specifications = Optional.ofNullable(aQuery.terms())
            .filter(str -> !str.isBlank())
            .map(str -> {
                final Specification<CategoryJpaEntity> nameLike = SpecificationUtils.like("name", str);
                final Specification<CategoryJpaEntity> descriptionLike = SpecificationUtils.like("description", str);
                return nameLike.or(descriptionLike);
            })
            .orElse(null);

        final var pageResult = categoryRepository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    private Category save(final Category aCategory) {
        return categoryRepository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }
}
