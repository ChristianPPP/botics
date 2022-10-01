package esfot.tesis.botics.repository;

import esfot.tesis.botics.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
