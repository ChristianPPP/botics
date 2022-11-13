package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Lab;
import esfot.tesis.botics.entity.enums.ELab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {
    Lab findByName(ELab name);
}
