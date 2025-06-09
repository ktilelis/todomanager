package com.ktilelis.todo;

import org.springframework.data.web.PagedModel;

import java.util.List;

public record TestCustomPage<T>(List<T> content, PagedModel.PageMetadata page) {
}
