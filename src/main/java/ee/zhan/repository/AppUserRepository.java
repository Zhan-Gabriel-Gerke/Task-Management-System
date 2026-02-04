package ee.zhan.repository;

import org.springframework.data.repository.CrudRepository;
import ee.zhan.entity.AppUser;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    Optional<AppUser> findAppUserByEmail(String email);
    boolean existsAppUsersByEmail(String email);
}
