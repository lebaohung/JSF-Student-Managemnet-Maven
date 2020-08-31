package com.synergix.controller;

import java.util.List;

public interface IBean<T> {
    List<T> getAll();

    String create();

    String getById(Integer id);

    void save(T model);

    void update(T model);

    void delete(Integer id);

}
