package com.quizapp.dao;

import java.util.List;

public interface DAO<T> {
    T getById(int id);
    List<T> getAll();
    boolean insert(T obj);
    boolean update(T obj);
    boolean delete(int id);
    int getCount();
}