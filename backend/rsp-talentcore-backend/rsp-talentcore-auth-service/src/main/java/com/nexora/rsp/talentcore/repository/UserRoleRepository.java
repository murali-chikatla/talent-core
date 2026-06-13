package com.nexora.rsp.talentcore.repository;

import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);
}