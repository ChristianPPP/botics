package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftwareRepository extends JpaRepository<Software, Long> {
    Software findByName(String name);
}
