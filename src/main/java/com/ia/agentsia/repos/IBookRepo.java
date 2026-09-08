package com.ia.agentsia.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

import com.ia.agentsia.models.Book;

public interface IBookRepo extends JpaRepository<Book, Integer> {

    // Querys personalizadas
    // @Query("FROM Book b WHERE b.name LIKE :bookName")
    // @Query("FROM Book b WHERE b.name LIKE :bookName")
    // List<Book> findByNameLike(@Param("bookName") String bookName);
    List<Book> findByNameLike(String name);

}
