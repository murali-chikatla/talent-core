package com.nexora.rsp.talentcore.service;

import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.ResourceRequest;
import com.nexora.rsp.talentcore.dto.ResourceResponse;
import com.nexora.rsp.talentcore.dto.ResourceSearchRequest;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id,
                                    ResourceRequest request);

    ResourceResponse getResource(Long id);

    PageResponse<ResourceResponse> searchResources(ResourceSearchRequest request);


    void deleteResource(Long id);
}
