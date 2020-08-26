package com.synergix;

import java.util.List;

public interface IService<T> {
    List<T> getAll();
    void save(T model);
    String edit(Integer id);
    void update(T model);
    void delete(Integer id);
}
