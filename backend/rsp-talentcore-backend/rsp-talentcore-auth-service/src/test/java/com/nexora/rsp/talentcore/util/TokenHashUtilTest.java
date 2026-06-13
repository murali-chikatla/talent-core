package com.nexora.rsp.talentcore.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenHashUtilTest {

    @Test
    void hashReturnsStableSha256HexValue() {

        String firstHash = TokenHashUtil.hash("refresh-token");
        String secondHash = TokenHashUtil.hash("refresh-token");

        assertThat(firstHash)
                .hasSize(64)
                .isEqualTo(secondHash)
                .isNotEqualTo(TokenHashUtil.hash("different-token"));
    }

    @Test
    void hashThrowsRuntimeExceptionForNullValue() {

        assertThatThrownBy(() -> TokenHashUtil.hash(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error hashing token");
    }
}
