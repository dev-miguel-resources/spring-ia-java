package com.ia.agentsia.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ia.agentsia.models.Book;
import com.ia.agentsia.repos.IBookRepo;
import com.ia.agentsia.services.IBookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {

    private final IBookRepo repo;

    @Override
    public Book save(Book book) {
        return repo.save(book);
    }

    @Override
    public List<Book> saveAll(List<Book> list) {
        return repo.saveAll(list);
    }

    @Override
    public Book update(Book book, Integer id) {
        return repo.save(book);
    }

    @Override
    public Book findById(Integer id) {
        return repo.findById(id).orElse(new Book());
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public List<Book> findAll() {
        return repo.findAll();
    }

}
