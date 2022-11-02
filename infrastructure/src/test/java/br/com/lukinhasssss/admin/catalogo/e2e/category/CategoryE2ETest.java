package br.com.lukinhasssss.admin.catalogo.e2e.category;

import br.com.lukinhasssss.admin.catalogo.E2ETest;
import br.com.lukinhasssss.admin.catalogo.domain.category.CategoryID;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.models.CategoryResponse;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import br.com.lukinhasssss.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import br.com.lukinhasssss.admin.catalogo.infrastructure.configuration.json.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2ETest {

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER =
        new MySQLContainer("mysql:latest")
            .withPassword("password")
            .withUsername("root")
            .withDatabaseName("adm_videos");
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Animes", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Animes")));

        listCategories(1, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));

        listCategories(2, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Séries")));

        listCategories(3, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Animes", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 1, "ani")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Animes")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", "A", true);
        givenACategory("Animes", "B", true);
        givenACategory("Séries", "C", true);

        listCategories(0, 3, "", "description", "desc")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Séries")))
            .andExpect(jsonPath("$.items[1].name", equalTo("Animes")))
            .andExpect(jsonPath("$.items[2].name", equalTo("Filmes")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveACategory(actualId.getValue());

        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertNotNull(actualCategory.updatedAt());
        assertNull(actualCategory.deletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCategory() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var aRequest = get("/categories/{id}", "123")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON);

        final var json = mvc.perform(aRequest)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", equalTo("Category with ID 123 was not found")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var actualId = givenACategory("Movies", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var aRequest = put("/categories/{id}", actualId.getValue())
            .contentType(APPLICATION_JSON)
            .content(Json.writeValueAsString(aRequestBody));

        mvc.perform(aRequest).andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToInactivateACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualId = givenACategory(expectedName, expectedDescription, true);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var aRequest = put("/categories/{id}", actualId.getValue())
            .contentType(APPLICATION_JSON)
            .content(Json.writeValueAsString(aRequestBody));

        mvc.perform(aRequest).andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToActivateACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenACategory(expectedName, expectedDescription, false);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var aRequest = put("/categories/{id}", actualId.getValue())
            .contentType(APPLICATION_JSON)
            .content(Json.writeValueAsString(aRequestBody));

        mvc.perform(aRequest).andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var actualId = givenACategory("Filmes", "A categoria mais assistida", true);

        final var aRequest = delete("/categories/{id}", actualId.getValue())
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON);

        mvc.perform(aRequest)
            .andExpect(status().isNoContent());

        assertFalse(categoryRepository.existsById(actualId.getValue()));
    }

    private CategoryID givenACategory(final String aName, final String aDescription, final boolean isActive) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(aName, aDescription, isActive);

        final var aRequest = post("/categories")
            .contentType(APPLICATION_JSON)
            .content(Json.writeValueAsString(aRequestBody));

        final var actualId = Objects.requireNonNull(
                mvc.perform(aRequest)
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse().getHeader("Location"))
            .replace("/categories/", "");

        return CategoryID.from(actualId);
    }

    private CategoryResponse retrieveACategory(final String anId) throws Exception {
        final var aRequest = get("/categories/{id}", anId)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON);

        final var json = mvc.perform(aRequest)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
    }

    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    private ResultActions listCategories(
        final int page,
        final int perPage,
        final String search,
        final String sort,
        final String direction
    ) throws Exception {
        final var aRequest = get("/categories")
            .queryParam("page", String.valueOf(page))
            .queryParam("perPage", String.valueOf(perPage))
            .queryParam("search", search)
            .queryParam("sort", sort)
            .queryParam("dir", direction)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON);

        return mvc.perform(aRequest);
    }
}
