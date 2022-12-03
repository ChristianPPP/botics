package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Reserve;

import java.util.List;

public interface ReserveService {
    List<Reserve> getReservesByUserId(Long idUser);
    void storeReserve(Reserve reserve);
    Reserve getReserveById(Long idReserve);
    List<Reserve> getReserves();
}
