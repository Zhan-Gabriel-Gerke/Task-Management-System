package ee.zhan.task.mapper;

import ee.zhan.task.dto.TaskCommentRespond;
import ee.zhan.task.entity.TasksCommentsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskCommentRespondMapper {

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "authorEmail", source = "author.email")
    TaskCommentRespond toRespond(TasksCommentsEntity entity);

}
