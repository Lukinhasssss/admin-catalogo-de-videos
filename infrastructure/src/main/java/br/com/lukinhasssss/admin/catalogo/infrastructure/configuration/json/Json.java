package br.com.lukinhasssss.admin.catalogo.infrastructure.configuration.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.Callable;

public enum Json {
    INSTANCE;

    public static ObjectMapper mapper() {
        return INSTANCE.mapper.copy();
    }

    public static String writeValueAsString(final Object obj) {
        return invoke(() -> INSTANCE.mapper.writeValueAsString(obj));
    }

    public static <T> T readValue(final String json, final Class<T> clazz) {
        return invoke(() -> INSTANCE.mapper.readValue(json, clazz));
    }

    private final ObjectMapper mapper = new Jackson2ObjectMapperBuilder()
        .dateFormat(new StdDateFormat())
        .featuresToDisable(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
            DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        )
        .modules(new JavaTimeModule(), new Jdk8Module(), afterburnerModule())
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .build();

    private AfterburnerModule afterburnerModule() {
        var module = new AfterburnerModule();
        // make Afterburner generate bytecode only for public getters/setters and fields
        // without this, Java 9+ complains of "Illegal reflective access"
        module.setUseValueClassLoader(false);
        return module;
    }

    private static <T> T invoke (final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * FAIL_ON_UNKNOWN_PROPERTIES: Se vier uma propriedade que meu objeto não mapeou, eu quero simplesmente ignorar
 * FAIL_ON_NULL_FOR_PRIMITIVES: Se vier null para uma propriedade primitiva, eu quero simplesmente ignorar
 *
 * JavaTimeModule: Este modulo faz com que o ObjectMapper entenda como serializar e deserializar datas que vieram do package JavaTime do Java8
 * Jdk8Module: Este modulo faz com que o Optional entenda como serializar e deserializar com optionals
 */