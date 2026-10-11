package com.example.librarymanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBook_shouldSaveBook() {

        BookRequest request = new BookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Ishika");
        request.setIsbn("TEST001");
        request.setAvailable(true);

        Book savedBook = new Book();
        savedBook.setId(1);
        savedBook.setTitle("Test Book");
        savedBook.setAuthor("Ishika");
        savedBook.setIsbn("TEST001");
        savedBook.setAvailable(true);

        when(bookRepository.save(any(Book.class)))
                .thenReturn(savedBook);

        Book result = bookService.createBook(request);

        assertEquals(savedBook, result);

        verify(bookRepository).save(any(Book.class));
    }
}