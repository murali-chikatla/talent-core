package com.nexora.rsp.talentcore.search;

import com.nexora.rsp.talentcore.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActiveStatusSearchValueTransformerTest {

    private final ActiveStatusSearchValueTransformer transformer =
            new ActiveStatusSearchValueTransformer();

    @Test
    void transformMapsBooleanToUserStatus() {

        assertThat(transformer.transform(true)).isEqualTo(UserStatus.ACTIVE);
        assertThat(transformer.transform(false)).isEqualTo(UserStatus.INACTIVE);
        assertThat(transformer.transform(null)).isEqualTo(UserStatus.INACTIVE);
    }
}
