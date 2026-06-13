package com.nexora.rsp.talentcore.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrincipalTest {

    @Test
    void getNameReturnsEmailUsedBySpringSecurity() {

        UserPrincipal principal = UserPrincipal.builder()
                .userId(1L)
                .email("murali@test.com")
                .roles(List.of("EMPLOYEE"))
                .build();

        assertThat(principal.getName()).isEqualTo("murali@test.com");
        assertThat(principal.getUserId()).isEqualTo(1L);
        assertThat(principal.getRoles()).containsExactly("EMPLOYEE");
    }
}
