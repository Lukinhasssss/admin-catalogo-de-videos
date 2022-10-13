package br.com.lukinhasssss.admin.catalogo.infrastructure.utils;

import org.springframework.data.jpa.domain.Specification;

public final class SpecificationUtils {

    private SpecificationUtils() {}

    /**
     * @param prop É a propriedade que queremos filtrar
     * @param term É o conteúdo que queremos filtrar
     */
    public static <T>Specification<T> like(final String prop, final String term) {
        return (root, query, cb) -> cb.like(cb.upper(root.get(prop)), like(term.toUpperCase()));
    }

    public static String like(final String term) {
        return "%" + term + "%";
    }
}
