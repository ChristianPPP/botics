package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Computer;

import java.util.List;

public interface ComputerService {
    List<Computer> getAll();
    Computer getComputerByHostName(String hostName);
    void saveComputer(Computer computer);
    void deleteComputer(Computer computer);
    Computer getComputerByID(Long id);
    void assignComputerToLab(Long idLab, Long idComputer);
}
