package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Computer;
import esfot.tesis.botics.repository.ComputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComputerServiceImpl implements ComputerService{
    @Autowired
    ComputerRepository computerRepository;

    @Override
    public List<Computer> getAll() {
        return computerRepository.findAll();
    }

    @Override
    public Computer getComputerByHostName(String hostName) {
        return computerRepository.getComputerByHostName(hostName);
    }

    @Override
    public void saveComputer(Computer computer) {
        computerRepository.save(computer);
    }

    @Override
    public void deleteComputer(Computer computer) {
        computerRepository.delete(computer);
    }

    @Override
    public Computer getComputerByID(Long id) {
        return computerRepository.getReferenceById(id);
    }

    @Override
    public List<Computer> getAllByLabReference(Long labReference) {
        return computerRepository.getComputersByLabReference(labReference);
    }

    @Override
    public Computer getComputerBySerialCpu(String serialCpu) {
        return computerRepository.findComputerBySerialCpu(serialCpu);
    }

    @Override
    public Computer getComputerBySerialMonitor(String serialMonitor) {
        return computerRepository.findComputerBySerialMonitor(serialMonitor);
    }

    @Override
    public List<Computer> filter(String filter, String value) {
        return computerRepository.filterComputer(filter, value);
    }
}

