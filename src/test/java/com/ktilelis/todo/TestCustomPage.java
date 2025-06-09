package com.ktilelis.todo;

import lombok.Data;

import java.util.List;

public class TestCustomPage<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCustomPage<?> that)) return false;

        if (getTotalPages() != that.getTotalPages()) return false;
        if (getTotalElements() != that.getTotalElements()) return false;
        return getContent() != null ? getContent().equals(that.getContent()) : that.getContent() == null;
    }

    @Override
    public int hashCode() {
        int result = getContent() != null ? getContent().hashCode() : 0;
        result = 31 * result + getTotalPages();
        result = 31 * result + (int) (getTotalElements() ^ (getTotalElements() >>> 32));
        return result;
    }
}
