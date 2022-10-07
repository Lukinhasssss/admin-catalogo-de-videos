package br.com.lukinhasssss.admin.catalogo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoryTest {

    @Test
    void testNewCategory() {
        assertNotNull(new Category());
    }

}