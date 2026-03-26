package ee.zhan.task.mapper;

import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.entity.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskSummaryMapper {

    //@Mapping(source = "author.email", target = "author.email")
    //@Mapping(source = "author.id", target = "author.id")
    TaskSummaryResponse toSummaryResponse(TaskEntity entity);
}
