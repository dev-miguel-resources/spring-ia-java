package com.ia.agentsia.dtos;

import java.util.List;

public record AuthorBook(
        String author,
        List<String> books) {

}
