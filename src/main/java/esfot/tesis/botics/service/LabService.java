package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Lab;
import esfot.tesis.botics.entity.enums.ELab;

import java.util.List;

public interface LabService {
    List<Lab> getAll();
    Lab getLabByName(ELab name);
    Lab getLabById(Long id);
    void saveLab(Lab lab);
}
