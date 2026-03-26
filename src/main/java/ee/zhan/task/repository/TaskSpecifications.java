package ee.zhan.task.repository;

import ee.zhan.task.entity.TaskEntity;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {

    public static Specification<TaskEntity> hasAuthorEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return cb.equal(root.get("author").get("email"), email);
        };
    }

    public static Specification<TaskEntity> hasAssigneeEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return  cb.equal(root.get("assignee").get("email"), email);
        };
    }
}
