package com.ia.agentsia.services.impl;

import java.util.List;
import java.util.function.Function;

import com.ia.agentsia.models.Book;
import com.ia.agentsia.repos.IBookRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookToolServiceImpl implements Function<BookToolServiceImpl.Request, BookToolServiceImpl.Response> {

    private final IBookRepo repo;

    public record Request(String bookName) {

    }

    public record Response(List<Book> books) {

    }

    @Override
    public Response apply(Request request) {

        List<Book> books = repo.findByNameLike("%" + request.bookName + "%");

        return new Response(books);

    }
}
