package com.nexora.rsp.talentcore.search;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentitySearchValueTransformerTest {

    private final IdentitySearchValueTransformer transformer =
            new IdentitySearchValueTransformer();

    @Test
    void transformReturnsOriginalValue() {

        Object value = new Object();

        assertThat(transformer.transform(value)).isSameAs(value);
    }
}
