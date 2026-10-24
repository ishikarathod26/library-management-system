package com.example.librarymanagement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookIssueRepository;
import com.example.librarymanagement.repository.BookRepository;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public BookService(BookRepository bookRepository, BookIssueRepository bookIssueRepository) {
        this.bookRepository = bookRepository;
        this.bookIssueRepository = bookIssueRepository;
    }

    public BookService(BookRepository bookRepository) {
        this(bookRepository, null);
    }

    public Book createBook(BookRequest request) {
        logger.info("Creating book: {}", request.getTitle());
        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setAvailable(request.isAvailable());

        try {
            return bookRepository.save(book);
        } catch (Exception e) {
            logger.error("Error while creating book: {}", request.getTitle(), e);
            throw e;
        }
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book updateBook(int id, Book book) {
        Book existingBook = bookRepository.findById(id).orElse(null);

        if (existingBook == null) {
            return null;
        }

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setAvailable(book.isAvailable());

        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(int id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: " + id));

        if (bookIssueRepository != null && bookIssueRepository.existsByBookIdAndReturnDateIsNull(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete book while it is currently issued to a student");
        }

        bookRepository.delete(book);
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchBooks(query.trim());
    }
}