package esfot.tesis.botics.auth.repository;

import esfot.tesis.botics.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String user_name);
    Boolean existsByUsername(String user_name);
    Boolean existsByEmail(String email);
}
