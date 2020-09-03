package com.synergix.repository;

import java.util.List;

public interface IPagingRepository<T> {
    List<T> getAllByPage(int page, int pageSize);

    int count();
}
