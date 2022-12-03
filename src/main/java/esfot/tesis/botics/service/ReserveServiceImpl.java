package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Reserve;
import esfot.tesis.botics.repository.ReserveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReserveServiceImpl implements ReserveService{
    @Autowired
    ReserveRepository reserveRepository;

    @Override
    public List<Reserve> getReservesByUserId(Long idUser) {
        return reserveRepository.findAllByUserId(idUser);
    }

    @Override
    public void storeReserve(Reserve reserve) {
        reserveRepository.save(reserve);
    }

    @Override
    public Reserve getReserveById(Long idReserve) {
        return reserveRepository.findByReserveId(idReserve);
    }

    @Override
    public List<Reserve> getReserves() {
        return reserveRepository.findAll();
    }
}
