package com.example.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.librarymanagement.entity.BookIssue;

public interface BookIssueRepository extends JpaRepository<BookIssue, Integer> {

}