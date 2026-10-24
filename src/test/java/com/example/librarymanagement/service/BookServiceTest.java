package com.example.librarymanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookIssueRepository;
import com.example.librarymanagement.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookIssueRepository bookIssueRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, bookIssueRepository);
    }

    private Book createBook(int id, String title, String author, String isbn, boolean available) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setAvailable(available);
        return book;
    }

    @Test
    void createBook_shouldSaveBook() {
        BookRequest request = new BookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Ishika");
        request.setIsbn("TEST001");
        request.setAvailable(true);

        Book savedBook = createBook(1, "Test Book", "Ishika", "TEST001", true);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book result = bookService.createBook(request);

        assertEquals(savedBook, result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void getAllBooks_shouldReturnList() {
        List<Book> books = List.of(
                createBook(1, "Book 1", "Author 1", "ISBN1", true),
                createBook(2, "Book 2", "Author 2", "ISBN2", false)
        );

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById_whenExists_shouldReturnBook() {
        Book book = createBook(1, "Book 1", "Author 1", "ISBN1", true);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1);

        assertNotNull(result);
        assertEquals("Book 1", result.getTitle());
    }

    @Test
    void getBookById_whenNotFound_shouldReturnNull() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        Book result = bookService.getBookById(99);

        assertNull(result);
    }

    @Test
    void updateBook_whenExists_shouldUpdateAndSave() {
        Book existing = createBook(1, "Old Title", "Author", "ISBN1", true);
        Book updates = createBook(1, "New Title", "New Author", "ISBN1-NEW", true);

        when(bookRepository.findById(1)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Book result = bookService.updateBook(1, updates);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        verify(bookRepository).save(existing);
    }

    @Test
    void deleteBook_whenBookIsAvailable_shouldDelete() {
        Book book = createBook(1, "Book 1", "Author 1", "ISBN1", true);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookIssueRepository.existsByBookIdAndReturnDateIsNull(1)).thenReturn(false);

        bookService.deleteBook(1);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_whenBookIsIssued_shouldThrowConflict() {
        Book book = createBook(1, "Book 1", "Author 1", "ISBN1", false);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookIssueRepository.existsByBookIdAndReturnDateIsNull(1)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> bookService.deleteBook(1));
        assertEquals(409, ex.getStatusCode().value());
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void searchBooks_withQuery_shouldCallRepository() {
        List<Book> books = List.of(createBook(1, "Spring In Action", "Craig Walls", "ISBN99", true));

        when(bookRepository.searchBooks("spring")).thenReturn(books);

        List<Book> result = bookService.searchBooks("spring");

        assertEquals(1, result.size());
        assertEquals("Spring In Action", result.get(0).getTitle());
    }
}