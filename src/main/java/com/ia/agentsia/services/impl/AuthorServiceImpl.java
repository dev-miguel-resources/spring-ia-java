package com.ia.agentsia.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ia.agentsia.models.Author;
import com.ia.agentsia.repos.IAuthorRepo;
import com.ia.agentsia.services.IAuthorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements IAuthorService {

    private final IAuthorRepo repo;

    @Override
    public Author save(Author author) {
        return repo.save(author);
    }

    @Override
    public List<Author> saveAll(List<Author> list) {
        return repo.saveAll(list);
    }

    @Override
    public Author update(Author author, Integer id) {
        return repo.save(author);
    }

    @Override
    public Author findById(Integer id) {
        return repo.findById(id).orElse(new Author());
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public List<Author> findAll() {
        return repo.findAll();
    }

}
