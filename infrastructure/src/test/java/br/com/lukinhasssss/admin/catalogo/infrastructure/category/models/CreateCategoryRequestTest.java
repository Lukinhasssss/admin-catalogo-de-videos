package br.com.lukinhasssss.admin.catalogo.infrastructure.category.models;

import br.com.lukinhasssss.admin.catalogo.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

@JacksonTest
class CreateCategoryRequestTest {

    @Autowired
    private JacksonTester<CreateCategoryRequest> json;

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var json = """
            {
              "name": "%s",
              "description": "%s",
              "is_active": "%s"
            }
            """.formatted(
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("description", expectedDescription)
            .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }

    @Test
    void testMarshall() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var request = new CreateCategoryRequest(
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        final var actualJson = json.write(request);

        Assertions.assertThat(actualJson)
            .hasJsonPathValue("$.name", expectedName)
            .hasJsonPathValue("$.description", expectedDescription)
            .hasJsonPathValue("$.is_active", expectedIsActive);
    }
}