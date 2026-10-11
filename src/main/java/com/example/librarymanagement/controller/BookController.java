package com.example.librarymanagement.controller;
import  java.util.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.service.BookService;
import com.example.librarymanagement.dto.BookRequest;
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/books")
    public Book createBook(@Valid @RequestBody BookRequest request) {
        return bookService.createBook(request);
    }
@GetMapping("/books")
public List<Book> getAllBooks() {
    return bookService.getAllBooks();
}
@GetMapping("/books/{id}")
public Book getBookById(@PathVariable int id) {
    return bookService.getBookById(id);
}
@PutMapping("/books/{id}")
public Book updateBook(@PathVariable int id, @RequestBody Book book) {
    return bookService.updateBook(id, book);
}
@DeleteMapping("/books/{id}")
public String deleteBook(@PathVariable int id) {
    bookService.deleteBook(id);
    return "Book deleted successfully";
}
}