package com.nexora.rsp.talentcore.util;

import com.nexora.rsp.talentcore.dto.PageRequestDto;
import com.nexora.rsp.talentcore.dto.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationUtilTest {

    @Test
    void toPageableUsesDefaultsWhenOptionalValuesAreBlankOrNull() {

        PageRequestDto request = new PageRequestDto();

        request.setPage(null);
        request.setSize(null);
        request.setSortBy("   ");
        request.setSortDirection("   ");

        Pageable pageable = PaginationUtil.toPageable(request);

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("userId")).isNotNull();
    }

    @Test
    void toPageableUsesRequestedPagingAndSort() {

        PageRequestDto request = new PageRequestDto();

        request.setPage(2);
        request.setSize(25);
        request.setSortBy("email");
        request.setSortDirection("DESC");

        Pageable pageable = PaginationUtil.toPageable(request);

        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(25);
        assertThat(pageable.getSort().getOrderFor("email").isDescending()).isTrue();
    }

    @Test
    void toPageResponseCopiesSpringPageMetadata() {

        Page<String> page = new PageImpl<>(
                List.of("one", "two"),
                Pageable.ofSize(2),
                4
        );

        PageResponse<String> response = PaginationUtil.toPageResponse(page);

        assertThat(response.getContent()).containsExactly("one", "two");
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(4);
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getFirst()).isTrue();
        assertThat(response.getLast()).isFalse();
        assertThat(response.getEmpty()).isFalse();
    }
}
