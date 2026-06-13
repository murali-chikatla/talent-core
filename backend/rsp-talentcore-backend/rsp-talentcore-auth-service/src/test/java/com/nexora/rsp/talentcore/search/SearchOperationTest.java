package com.nexora.rsp.talentcore.search;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchOperationTest {

    @Test
    void valuesExposeSupportedSearchOperations() {

        assertThat(SearchOperation.values())
                .containsExactly(SearchOperation.EQUAL, SearchOperation.LIKE);
        assertThat(SearchOperation.valueOf("EQUAL")).isEqualTo(SearchOperation.EQUAL);
        assertThat(SearchOperation.valueOf("LIKE")).isEqualTo(SearchOperation.LIKE);
    }
}
