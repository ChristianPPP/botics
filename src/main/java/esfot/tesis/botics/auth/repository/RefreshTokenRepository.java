package esfot.tesis.botics.auth.repository;

import esfot.tesis.botics.auth.entity.RefreshToken;
import esfot.tesis.botics.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);

    RefreshToken getByUser(User user);
}