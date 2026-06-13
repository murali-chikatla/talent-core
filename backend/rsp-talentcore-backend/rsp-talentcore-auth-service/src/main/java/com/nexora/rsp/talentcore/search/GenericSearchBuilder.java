package com.nexora.rsp.talentcore.search;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GenericSearchBuilder {

    private static final String PATH_SEPARATOR = "\\.";
    private static final String LIKE_PATTERN = "%%%s%%";

    private final AutowireCapableBeanFactory beanFactory;

    public <T> Specification<T> build(Object request) {

        return getFields(request.getClass())
                .filter(field -> field.isAnnotationPresent(SearchField.class))
                .map(field -> this.<T>toSpecification(field, request))
                .flatMap(Optional::stream)
                .reduce(this.<T>emptySpecification(), Specification::and);
    }

    private Stream<Field> getFields(Class<?> type) {

        List<Field> fields = new ArrayList<>();
        Class<?> currentType = type;

        while (currentType != null) {
            fields.addAll(Arrays.asList(currentType.getDeclaredFields()));
            currentType = currentType.getSuperclass();
        }

        return fields.stream();
    }

    private <T> Optional<Specification<T>> toSpecification(Field field, Object request) {

        SearchField searchField = field.getAnnotation(SearchField.class);

        return Optional.ofNullable(readValue(field, request))
                .map(this::normalize)
                .filter(this::hasValue)
                .map(value -> transform(searchField, value))
                .map(value -> createSpecification(searchField, field.getName(), value));
    }

    private Object readValue(Field field, Object request) {

        try {
            field.setAccessible(true);
            return field.get(request);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Unable to read search field " + field.getName(), ex);
        }
    }

    private Object normalize(Object value) {

        return value instanceof String text ? text.trim() : value;
    }

    private boolean hasValue(Object value) {

        return !(value instanceof String text && text.isBlank());
    }

    private Object transform(SearchField searchField, Object value) {

        return beanFactory.createBean(searchField.transformer())
                .transform(value);
    }

    private <T> Specification<T> createSpecification(SearchField searchField, String fieldName, Object value) {

        return (root, query, builder) -> {
            query.distinct(true);

            Path<?> path = resolvePath(
                    root,
                    resolvePathName(searchField, fieldName)
            );

            return switch (searchField.operation()) {
                case LIKE -> builder.like(
                        builder.lower(path.as(String.class)),
                        LIKE_PATTERN.formatted(value.toString().toLowerCase())
                );
                case EQUAL -> builder.equal(path, value);
            };
        };
    }

    private String resolvePathName(SearchField searchField, String fieldName) {

        return Optional.ofNullable(searchField.path())
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .orElse(fieldName);
    }

    private <T> Path<?> resolvePath(Root<T> root, String path) {

        String[] fields = path.split(PATH_SEPARATOR);
        From<?, ?> currentPath = root;

        for (int index = 0; index < fields.length - 1; index++) {
            currentPath = currentPath.join(fields[index], JoinType.LEFT);
        }

        return currentPath.get(fields[fields.length - 1]);
    }

    private <T> Specification<T> emptySpecification() {

        return (root, query, builder) -> builder.conjunction();
    }
}
