package org.bhaskar.user;

import org.bhaskar.model.User;
import org.springframework.stereotype.Service;

@Service
public record UserService(UserRepository userRepository) {

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
