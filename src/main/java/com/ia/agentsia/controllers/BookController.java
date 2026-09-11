package com.ia.agentsia.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ia.agentsia.models.Book;
import com.ia.agentsia.services.IBookService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class BookController {

    private final IBookService service;

    @GetMapping
    public ResponseEntity<List<Book>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Book> save(@RequestBody Book book) {
        Book obj = service.save(book);
        return ResponseEntity.created(URI.create("http://localhost:8080/Books/" + obj.getIdBook())).body(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@RequestBody Book book, @PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.update(book, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteById(@PathVariable("id") Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
