package com.example.librarymanagement.service;
import  java.util.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookRepository;

@Service
public class BookService {

private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
public void deleteBook(int id) {
    bookRepository.deleteById(id);
}
}