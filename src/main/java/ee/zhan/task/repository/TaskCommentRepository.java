package ee.zhan.task.repository;

import ee.zhan.task.entity.TaskEntity;
import ee.zhan.task.entity.TasksCommentsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskCommentRepository extends JpaRepository<TasksCommentsEntity, Long> {
    Optional<TasksCommentsEntity> findById(Long id);

    Page<TasksCommentsEntity> findAllByTaskId(Long taskId, Pageable pageable);
}
