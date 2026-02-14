package ee.zhan.mapper.Task;


import ee.zhan.dto.Task.CreateTaskCommand;
import ee.zhan.entity.AppUserEntity;
import ee.zhan.entity.TaskEntity;
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
    public abstract TaskEntity toEntity(CreateTaskCommand command);

    protected AppUserEntity mapUserIdToEntity(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return entityManager.getReference(AppUserEntity.class, authorId);
    }
}
