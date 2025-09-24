package com.hotpack.krocs.domain.user.facade;

import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryFacade {

    private final UserRepository userRepository;

    public User findActiveUserByUserId(Long userId) {
        return userRepository.findUserByUserId(userId);
    }
}
