package com.nexora.rsp.talentcore.api.controller;

import com.nexora.rsp.talentcore.api.ResourceApi;


import com.nexora.rsp.talentcore.dto.ResourceRequest;
import com.nexora.rsp.talentcore.dto.ResourceResponse;
import com.nexora.rsp.talentcore.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController implements ResourceApi {

    private final ResourceService resourceService;

    @Override
    public ResourceResponse createResource(
            ResourceRequest request) {
        return resourceService.createResource(request);
    }



    @Override
    public ResourceResponse getResource(Long id) {
        return resourceService.getResource(id);
    }

    @Override
    public ResourceResponse updateResource(
            Long id,
            ResourceRequest request) {
        return resourceService.updateResource(id, request);
    }

    @Override
    public void deleteResource(Long id) {
        resourceService.deleteResource(id);
    }

    public Object me(Authentication authentication) {
        return authentication;
    }
}