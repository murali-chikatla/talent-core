package com.nexora.rsp.talentcore.mapper;

import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.domain.UserRole;
import com.nexora.rsp.talentcore.domain.UserStatus;
import com.nexora.rsp.talentcore.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        return toResponse(
                user,
                user.getUserRoles()
                        .stream()
                        .map(UserRole::getRole)
                        .filter(Objects::nonNull)
                        .map(role -> role.getRoleCode())
                        .toList()
        );
    }

    public UserResponse toResponse(User user, Collection<String> roles) {

        UserResponse response = new UserResponse();

        response.setUserId(user.getUserId());
        response.setEmployeeCode(user.getEmployeeCode());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setActive(UserStatus.isActive(user.getUserStatus()));
        response.setRoles(List.copyOf(roles));

        return response;
    }
}
