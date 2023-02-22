package ru.baz.aisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.baz.aisa.entity.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByPhone(String phone);

}
