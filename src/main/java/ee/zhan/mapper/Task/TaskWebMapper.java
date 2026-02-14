package ee.zhan.mapper.Task;

import ee.zhan.dto.Task.CreateTaskCommand;
import ee.zhan.dto.Task.CreateTaskRequest;
import ee.zhan.security.AppUserAdapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskWebMapper {

    @Mapping(target = "authorId", source = "userAdapter.id")
    CreateTaskCommand toCommand(CreateTaskRequest request, AppUserAdapter userAdapter);
}
