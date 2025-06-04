package com.ktilelis.todo.todomanagement.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoMapper {

    TodoResponseDto toDto(TodoEntry entity);

//    @Mapping(target = "id", expression = "java(null)")
//    @Mapping(target = "isDone", constant = "false")
//    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
//    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    TodoEntry toEntity(TodoRequestDto dto);
}