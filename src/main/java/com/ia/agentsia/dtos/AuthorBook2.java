package com.ia.agentsia.dtos;

import java.util.List;

import com.ia.agentsia.models.Book;

public record AuthorBook2(
        String author,
        List<Book> books) {

}
