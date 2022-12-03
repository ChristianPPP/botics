package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Response;
import esfot.tesis.botics.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseServiceImpl implements ResponseService {
    @Autowired
    ResponseRepository responseRepository;

    @Override
    public void storeResponse(Response response) {
        responseRepository.save(response);
    }
}
