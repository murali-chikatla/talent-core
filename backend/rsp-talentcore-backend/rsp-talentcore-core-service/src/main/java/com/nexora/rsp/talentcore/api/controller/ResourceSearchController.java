package com.nexora.rsp.talentcore.api.controller;

import com.nexora.rsp.talentcore.api.ResourceSearchApi;
import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.ResourceResponse;
import com.nexora.rsp.talentcore.dto.ResourceSearchRequest;
import com.nexora.rsp.talentcore.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResourceSearchController implements ResourceSearchApi {

    private final ResourceService resourceService;

    @Override
    public ResponseEntity<PageResponse<ResourceResponse>> searchResources(ResourceSearchRequest request) {

        return ResponseEntity.ok(resourceService.searchResources(request));
    }
}
