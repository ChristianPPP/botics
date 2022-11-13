package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Lab;
import esfot.tesis.botics.entity.enums.ELab;
import esfot.tesis.botics.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabServiceImpl implements LabService{
    @Autowired
    LabRepository labRepository;

    @Override
    public List<Lab> getAll() {
        return labRepository.findAll();
    }

    @Override
    public Lab getLabByName(ELab name) {
        return labRepository.findByName(name);
    }

    @Override
    public Lab getLabById(Long id) {
        return labRepository.getReferenceById(id);
    }

    @Override
    public void saveLab(Lab lab) {
        labRepository.save(lab);
    }
}
