package com.nexora.rsp.talentcore.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.List;

@Getter
@Builder
public class UserPrincipal implements AuthenticatedPrincipal {

    private Long userId;

    private String email;

    private List<String> roles;

    @Override
    public String getName() {

        return email;
    }
}
