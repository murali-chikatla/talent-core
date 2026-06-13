package com.nexora.rsp.talentcore.search;

import com.nexora.rsp.talentcore.domain.UserStatus;

public class ActiveStatusSearchValueTransformer implements SearchValueTransformer {

    @Override
    public Object transform(Object value) {

        return UserStatus.fromActive(Boolean.TRUE.equals(value));
    }
}
