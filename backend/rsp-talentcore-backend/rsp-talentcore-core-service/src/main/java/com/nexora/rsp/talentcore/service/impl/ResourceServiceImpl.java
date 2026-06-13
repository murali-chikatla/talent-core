package com.nexora.rsp.talentcore.service.impl;

import com.nexora.rsp.talentcore.domain.Resource;
import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.ResourceRequest;
import com.nexora.rsp.talentcore.dto.ResourceResponse;
import com.nexora.rsp.talentcore.dto.ResourceSearchRequest;
import com.nexora.rsp.talentcore.enums.ResourceStatus;
import com.nexora.rsp.talentcore.execeptions.ResourceNotFoundException;
import com.nexora.rsp.talentcore.repository.ResourceRepository;
import com.nexora.rsp.talentcore.search.GenericSearchBuilder;
import com.nexora.rsp.talentcore.service.ResourceService;
import com.nexora.rsp.talentcore.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final GenericSearchBuilder genericSearchBuilder;

    @Override
    public ResourceResponse createResource(ResourceRequest request) {

        Resource resource = new Resource();

        resource.setEmployeeId(request.getEmployeeId());
        resource.setFirstName(request.getFirstName());
        resource.setLastName(request.getLastName());
        resource.setEmail(request.getEmail());
        resource.setMobile(request.getMobile());
        resource.setExperienceYears(BigDecimal.valueOf(request.getExperienceYears()));
        resource.setPrimarySkill(request.getPrimarySkill());
        resource.setStatus(ResourceStatus.valueOf(request.getStatus()));

        Resource saved = resourceRepository.save(resource);

        return mapToResponse(saved);
    }

    @Override
    public ResourceResponse updateResource(Long id,
                                           ResourceRequest request) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        resource.setFirstName(request.getFirstName());
        resource.setLastName(request.getLastName());
        resource.setEmail(request.getEmail());
        resource.setMobile(request.getMobile());
        resource.setExperienceYears(BigDecimal.valueOf(request.getExperienceYears()));
        resource.setPrimarySkill(request.getPrimarySkill());
        resource.setStatus(ResourceStatus.valueOf(request.getStatus()));

        Resource updated = resourceRepository.save(resource);

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        return mapToResponse(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResourceResponse> searchResources(ResourceSearchRequest request) {

        Pageable pageable = PaginationUtil.toPageable(request);

        Specification<Resource> specification =
                genericSearchBuilder.build(request);

        Page<Resource> resources =
                resourceRepository.findAll(
                        specification,
                        pageable
                );

        return PaginationUtil.toPageResponse(
                resources.map(this::mapToResponse)
        );
    }



    @Override
    public void deleteResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        resource.setDeleted(true);

        resourceRepository.save(resource);
    }

    private ResourceResponse mapToResponse(Resource resource) {

        ResourceResponse response = new ResourceResponse();

        response.setId(resource.getId());
        response.setEmployeeId(resource.getEmployeeId());
        response.setFirstName(resource.getFirstName());
        response.setLastName(resource.getLastName());
        response.setEmail(resource.getEmail());
        response.setMobile(resource.getMobile());
        response.setExperienceYears(
                resource.getExperienceYears() == null
                        ? null
                        : resource.getExperienceYears().doubleValue()
        );
        response.setPrimarySkill(resource.getPrimarySkill());
        response.setStatus(resource.getStatus().name());

        return response;
    }
}
