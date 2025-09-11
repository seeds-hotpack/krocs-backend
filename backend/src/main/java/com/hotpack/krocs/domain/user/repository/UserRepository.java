package com.hotpack.krocs.domain.user.repository;

import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByAccountIdAndStatus(String accountId, Status status);
}


