package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.baz.aisa.entity.User;
import ru.baz.aisa.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(String userName, String userPhone) {

        User user = userRepository.findUserByPhone(userPhone).orElse(null);

        if (user == null) {
            user = createUser(userName, userPhone);
        }

        if (user == null) {
            throw new RuntimeException("User is not found and cannot be created");
        }

        return user;
    }


    @Transactional
    public User createUser(String userName, String userPhone) {
        User user = new User(null, userName, userPhone);

        userRepository.saveAndFlush(user);

        return user;
    }
}
