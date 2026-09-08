package com.ia.agentsia.services;

import java.util.List;

public interface ICRUD<T, ID> {

    T save(T t);

    List<T> saveAll(List<T> t);

    T update(T t, ID id);

    List<T> findAll();

    T findById(ID id);

    void delete(ID id);

}
