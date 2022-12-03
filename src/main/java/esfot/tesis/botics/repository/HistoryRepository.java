package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query(value = "SELECT * FROM history WHERE state = TRUE AND computer_reference = ?1", nativeQuery = true)
    History getHistoryByStateAndComputerReference(Long computerReference);
}
