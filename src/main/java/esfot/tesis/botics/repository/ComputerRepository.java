package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Computer getComputerByHostName(String hostName);
    List<Computer> getComputersByLabReference(Long labReference);
    Computer findComputerBySerialCpu(String serialCpu);
    Computer findComputerBySerialMonitor(String serialMonitor);

    @Query(value="SELECT * FROM computers WHERE ?1 = ?2", nativeQuery = true)
    List<Computer> filterComputer(String filter, String value);
}
