package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Software;

public interface SoftwareService {
    void saveSoftware(Software software);
    Software getSoftwareByName(String name);
    Software getSoftware(Long id);
}
