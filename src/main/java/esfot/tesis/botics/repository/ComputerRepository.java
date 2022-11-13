package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Computer getComputerByHostName(String hostName);

    @Modifying
    @Query(value = "UPDATE computers SET lab_id = ?1 WHERE id = ?2", nativeQuery = true)
    void assignLab(Long idLab, Long idComputer);

    @Modifying
    @Query(value = "UPDATE computers SET lab_id = 0 WHERE id = ?1", nativeQuery = true)
    void unassignLab(Long idComputer);
}
