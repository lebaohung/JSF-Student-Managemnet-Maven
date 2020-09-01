package com.synergix.controller;

import java.util.List;

public interface IBean<T> {

    String moveToListPage();

    List<T> getAll();

    void create();

    void getEdit(Integer id);

    void save(T model);

    void update(T model);

    void delete(Integer id);

    void cancelEdit();

    void cancelAdd();

}
