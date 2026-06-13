package com.nexora.rsp.talentcore.api.controllers;

import com.nexora.rsp.talentcore.api.UserSearchApi;
import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.UserResponse;
import com.nexora.rsp.talentcore.dto.UserSearchRequest;
import com.nexora.rsp.talentcore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserSearchController implements UserSearchApi {
    private final UserService userService;

    @Override
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(UserSearchRequest request) {

        return ResponseEntity.ok(userService.searchUsers(request));
    }
}
