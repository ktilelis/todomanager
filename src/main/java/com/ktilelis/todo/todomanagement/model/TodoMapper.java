package com.ktilelis.todo.todomanagement.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoMapper {

    TodoResponseDto toDto(TodoEntry entity);

    TodoEntry toEntity(TodoRequestDto dto);
}