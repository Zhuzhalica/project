package org.example.project.repositories;


import java.util.Optional;
import org.example.project.data.models.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByLogin(String login);
}
