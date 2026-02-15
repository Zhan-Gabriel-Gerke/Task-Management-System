package ee.zhan.task.mapper;

import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.CreateTaskRequest;
import ee.zhan.user.AppUserAdapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskWebMapper {

    @Mapping(target = "authorId", source = "userAdapter.id")
    CreateTaskCommand toCommand(CreateTaskRequest request, AppUserAdapter userAdapter);
}
