package com.synergix.controller;

import java.util.List;

public interface IPaging<T> {

    List<T> getAllByPage();

    void next();

    void previous();
}
