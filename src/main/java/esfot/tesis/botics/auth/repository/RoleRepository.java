package esfot.tesis.botics.auth.repository;

import esfot.tesis.botics.auth.entity.Role;
import esfot.tesis.botics.auth.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
