package ee.zhan.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {
    Optional<AppUserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<AppUserEntity> findById(Long userId);
    boolean existsById(Long userId);

}