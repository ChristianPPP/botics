package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {
    List<Reserve> findAllByUserId(Long idUser);

    @Query(value = "SELECT * FROM reserves WHERE id = ?1", nativeQuery = true)
    Reserve findByReserveId(Long idReserve);
}
