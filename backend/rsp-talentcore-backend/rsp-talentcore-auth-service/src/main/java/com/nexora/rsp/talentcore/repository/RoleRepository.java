package com.nexora.rsp.talentcore.repository;

import com.nexora.rsp.talentcore.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCode(String roleCode);

    List<Role> findByRoleCodeIn(Collection<String> roleCodes);
}
