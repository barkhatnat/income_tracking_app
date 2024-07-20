package ru.barkhatnat.income_tracking.api.repository;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.repositories.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepositoryTest_SaveOne_ReturnSavedUser() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        User savedUser = userRepository.save(user);
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getUsername()).isEqualTo("username");
    }

    @Test
    public void UserRepositoryTest_FindAll_ReturnAllSaved() {
        User user1 = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        User user2 = new User("username2", "password2", "email2@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user1);
        userRepository.save(user2);
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users.size()).isEqualTo(2);
        Assertions.assertThat(users.get(0).getId()).isNotEqualTo(users.get(1).getId());
    }

    @Test
    public void UserRepositoryTest_FindById_ReturnExistingUser() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    public void UserRepositoryTest_FindById_ReturnEmptyWhenNotFound() {
        UUID uuid = UUID.randomUUID();
        Optional<User> foundUser = userRepository.findById(uuid);
        Assertions.assertThat(foundUser).isEmpty();
    }

    @Test
    public void UserRepositoryTest_FindByUsername_ReturnExistingUser() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("email@email.com");
        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void UserRepositoryTest_FindByUsername_ReturnEmptyWhenNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent");
        Assertions.assertThat(foundUser).isEmpty();
    }

    @Test
    public void UserRepositoryTest_Delete_RemoveExistingUser() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        Assertions.assertThat(foundUser).isEmpty();
    }
}
