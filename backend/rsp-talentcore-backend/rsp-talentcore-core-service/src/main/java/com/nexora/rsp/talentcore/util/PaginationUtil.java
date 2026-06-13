package com.nexora.rsp.talentcore.util;

import com.nexora.rsp.talentcore.dto.PageRequestDto;
import com.nexora.rsp.talentcore.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.function.Predicate;

public final class PaginationUtil {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final String DEFAULT_SORT_BY = "id";
    private static final String DEFAULT_SORT_DIRECTION = "ASC";

    private PaginationUtil() {
    }

    public static Pageable toPageable(PageRequestDto request) {

        Sort sort = Sort.by(
                getSortDirection(request),
                getSortBy(request)
        );

        return PageRequest.of(
                getPage(request),
                getSize(request),
                sort
        );
    }

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {

        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    private static Integer getPage(PageRequestDto request) {

        return Optional.ofNullable(request.getPage())
                .orElse(DEFAULT_PAGE);
    }

    private static Integer getSize(PageRequestDto request) {

        return Optional.ofNullable(request.getSize())
                .orElse(DEFAULT_SIZE);
    }

    private static String getSortBy(PageRequestDto request) {

        return Optional.ofNullable(request.getSortBy())
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .orElse(DEFAULT_SORT_BY);
    }

    private static Sort.Direction getSortDirection(PageRequestDto request) {

        return Sort.Direction.fromString(
                Optional.ofNullable(request.getSortDirection())
                        .map(String::trim)
                        .filter(Predicate.not(String::isBlank))
                        .orElse(DEFAULT_SORT_DIRECTION)
        );
    }
}
