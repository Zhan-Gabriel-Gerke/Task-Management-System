package ee.zhan.task.mapper;


import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.user.entity.AppUserEntity;
import ee.zhan.task.entity.TaskEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class TaskEntityMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "assignee", ignore = true)

    public abstract TaskEntity toEntity(CreateTaskCommand command);

    protected AppUserEntity mapAuthorIdToEntity(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return entityManager.getReference(AppUserEntity.class, authorId);
    }
}
