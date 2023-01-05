package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Computer;

import java.util.List;

public interface ComputerService {
    List<Computer> getAll();
    Computer getComputerByHostName(String hostName);
    void saveComputer(Computer computer);
    void deleteComputer(Computer computer);
    Computer getComputerByID(Long id);
    List<Computer> getAllByLabReference(Long labReference);
    Computer getComputerBySerialCpu(String serialCpu);
    Computer getComputerBySerialMonitor(String serialMonitor);
    List<Computer> filter(String filter, String value);
}
