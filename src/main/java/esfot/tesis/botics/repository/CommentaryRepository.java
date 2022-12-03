package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Commentary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
    List<Commentary> findAllByUserId(Long idUser);

    @Query(value = "SELECT * FROM commentaries WHERE NOT user_id = ?1", nativeQuery = true)
    List<Commentary> findAllByUserIdNot(Long idUser);

    Commentary findCommentaryById(Long commentaryId);
}
