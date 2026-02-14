package ee.zhan.mapper.Task;

import ee.zhan.dto.Task.TaskSummaryResponse;
import ee.zhan.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface TaskSummaryMapper {

    @Mapping(source = "author.email", target = "author.email")
    @Mapping(source = "author.id", target = "author.id")
    @Mapping(target = "author", ignore = true)
    TaskSummaryResponse toSummaryResponse(TaskEntity entity);
}
