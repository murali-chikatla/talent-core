package com.nexora.rsp.talentcore.domain;

public enum UserStatus {

    ACTIVE,

    INACTIVE;

    public static UserStatus fromActive(Boolean active) {

        return Boolean.TRUE.equals(active) ? ACTIVE : INACTIVE;
    }

    public static Boolean isActive(UserStatus userStatus) {

        return ACTIVE.equals(userStatus);
    }
}
