package com.ia.agentsia.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ia.agentsia.models.Author;

public interface IAuthorRepo extends JpaRepository<Author, Integer> {

}
