package ee.zhan.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {
    Optional<TaskEntity> findById(Long id);
    @Query("SELECT t FROM TaskEntity t WHERE (:email IS NULL OR t.author.email = :email)")
    Page<TaskEntity> findByOptionalEmail(String email, Pageable pageable);
}
