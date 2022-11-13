package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Software;
import esfot.tesis.botics.repository.SoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SoftwareServiceImpl implements SoftwareService{
    @Autowired
    SoftwareRepository softwareRepository;

    @Override
    public void saveSoftware(Software software) {
        softwareRepository.save(software);
    }

    @Override
    public Software getSoftwareByName(String name) {
        return softwareRepository.findByName(name);
    }

    @Override
    public Software getSoftware(Long id) {
        return softwareRepository.getReferenceById(id);
    }
}
