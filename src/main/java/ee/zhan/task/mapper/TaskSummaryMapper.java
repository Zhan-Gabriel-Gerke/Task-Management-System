package ee.zhan.task.mapper;

import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskSummaryMapper {

    @Mapping(source = "author.email", target = "author.email")
    @Mapping(source = "author.id", target = "author.id")
    @Mapping(target = "author", ignore = true)
    TaskSummaryResponse toSummaryResponse(TaskEntity entity);
}
