
package com.example.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.librarymanagement.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

}