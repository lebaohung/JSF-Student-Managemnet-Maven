package com.ptit.repository;

import java.sql.SQLException;
import java.util.List;

public interface IRepository<T> {
    List<T> getAll();

    void save(T model);

    T getById(Integer id) throws SQLException;

    void update(T model);

    void delete(Integer id) throws SQLException;
}
