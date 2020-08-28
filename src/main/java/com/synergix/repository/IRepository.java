package com.synergix.repository;

import java.util.List;

public interface IRepository<T> {
    List<T> getAll();
    void save(T model);
    T getById(Integer id);
    void update(T model);
    void delete(Integer id);
}
