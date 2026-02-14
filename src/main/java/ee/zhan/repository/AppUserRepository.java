package ee.zhan.repository;

import ee.zhan.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {
    Optional<AppUserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<AppUserEntity> findById(Long userId);
    boolean existsById(Long userId);

}