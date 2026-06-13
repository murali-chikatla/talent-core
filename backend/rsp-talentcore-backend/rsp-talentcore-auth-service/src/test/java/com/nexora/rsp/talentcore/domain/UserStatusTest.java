package com.nexora.rsp.talentcore.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserStatusTest {

    @Test
    void fromActiveMapsBooleanToStatus() {

        assertThat(UserStatus.fromActive(true)).isEqualTo(UserStatus.ACTIVE);
        assertThat(UserStatus.fromActive(false)).isEqualTo(UserStatus.INACTIVE);
        assertThat(UserStatus.fromActive(null)).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void isActiveReturnsTrueOnlyForActiveStatus() {

        assertThat(UserStatus.isActive(UserStatus.ACTIVE)).isTrue();
        assertThat(UserStatus.isActive(UserStatus.INACTIVE)).isFalse();
        assertThat(UserStatus.isActive(null)).isFalse();
    }
}
