package com.nexora.rsp.talentcore.repository;


import com.nexora.rsp.talentcore.domain.RefreshToken;
import com.nexora.rsp.talentcore.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUser(User user);
}
