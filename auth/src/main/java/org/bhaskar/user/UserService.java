package org.bhaskar.user;

import org.bhaskar.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
