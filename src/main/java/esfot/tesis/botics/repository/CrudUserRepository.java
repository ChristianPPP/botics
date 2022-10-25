package esfot.tesis.botics.repository;

import esfot.tesis.botics.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrudUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE first_name = ?1 AND " +
            "id IN (SELECT user_id FROM user_roles WHERE role_id = 3)", nativeQuery = true)
    User getInternUserByFirstName(String firstName);

    @Query(value = "SELECT * FROM users WHERE id IN (SELECT user_id FROM user_roles WHERE role_id = 3)", nativeQuery = true)
    List<User> getInternUsers();

    @Query(value = "SELECT * FROM users WHERE id = ?1 AND id IN(SELECT user_id FROM user_roles WHERE role_id = 3)", nativeQuery = true)
    User getById(Long id);
}
