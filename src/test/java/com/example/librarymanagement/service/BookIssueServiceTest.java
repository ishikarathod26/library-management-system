package com.example.librarymanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.example.librarymanagement.dto.BookIssueResponse;
import com.example.librarymanagement.dto.IssueBookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookIssue;
import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.repository.BookIssueRepository;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookIssueServiceTest {

    @Mock
    private BookIssueRepository bookIssueRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private StudentRepository studentRepository;

    private BookIssueService bookIssueService;

    @BeforeEach
    void setUp() {
        bookIssueService = new BookIssueService(
                bookIssueRepository,
                bookRepository,
                studentRepository
        );
    }

    private Student createStudent() {
        Student student = new Student();
        student.setId(1);
        student.setName("Ishika Rathod");
        student.setEmail("ishika.updated@gmail.com");
        return student;
    }

    private Book createBook(boolean available) {
        Book book = new Book();
        book.setId(3);
        book.setTitle("Database Basics");
        book.setAuthor("Ishika");
        book.setIsbn("ISBN200");
        book.setAvailable(available);
        return book;
    }

    private IssueBookRequest createRequest(LocalDate dueDate) {
        IssueBookRequest request = new IssueBookRequest();
        request.setStudentId(1);
        request.setBookId(3);
        request.setDueDate(dueDate);
        return request;
    }

    @Test
    void shouldIssueBookWhenBookIsAvailable() {
        Student student = createStudent();
        Book book = createBook(true);

        when(studentRepository.findById(1))
                .thenReturn(Optional.of(student));

        when(bookRepository.findById(3))
                .thenReturn(Optional.of(book));

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookIssueRepository.save(any(BookIssue.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookIssueResponse response = bookIssueService.issueBook(
                createRequest(LocalDate.now().plusDays(7))
        );

        assertEquals(1, response.getStudentId());
        assertEquals(3, response.getBookId());
        assertEquals("Database Basics", response.getBookTitle());
        assertNull(response.getReturnDate());
        assertFalse(book.isAvailable());

        verify(bookIssueRepository).save(any(BookIssue.class));
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectIssueWhenBookIsUnavailable() {
        Student student = createStudent();
        Book book = createBook(false);

        when(studentRepository.findById(1))
                .thenReturn(Optional.of(student));

        when(bookRepository.findById(3))
                .thenReturn(Optional.of(book));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookIssueService.issueBook(
                        createRequest(LocalDate.now().plusDays(7))
                )
        );

        assertEquals(409, exception.getStatusCode().value());

        verify(bookIssueRepository, never())
                .save(any(BookIssue.class));
    }

    @Test
    void shouldReturnBookAndUpdateAvailability() {
        Student student = createStudent();
        Book book = createBook(false);

        BookIssue issue = new BookIssue();
        issue.setId(3);
        issue.setStudent(student);
        issue.setBook(book);
        issue.setIssueDate(LocalDate.now().minusDays(2));
        issue.setDueDate(LocalDate.now().plusDays(5));

        when(bookIssueRepository.findById(3))
                .thenReturn(Optional.of(issue));

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookIssueRepository.save(any(BookIssue.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookIssueResponse response = bookIssueService.returnBook(3);

        assertEquals(3, response.getBookId());
        assertEquals(LocalDate.now(), response.getReturnDate());
        assertTrue(book.isAvailable());

        verify(bookRepository).save(book);
        verify(bookIssueRepository).save(issue);
    }

    @Test
    void shouldRejectReturningBookTwice() {
        Student student = createStudent();
        Book book = createBook(true);

        BookIssue issue = new BookIssue();
        issue.setId(1);
        issue.setStudent(student);
        issue.setBook(book);
        issue.setIssueDate(LocalDate.now().minusDays(3));
        issue.setDueDate(LocalDate.now().plusDays(4));
        issue.setReturnDate(LocalDate.now());

        when(bookIssueRepository.findById(1))
                .thenReturn(Optional.of(issue));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookIssueService.returnBook(1)
        );

        assertEquals(409, exception.getStatusCode().value());

        verify(bookRepository, never()).save(any(Book.class));
        verify(bookIssueRepository, never()).save(any(BookIssue.class));
    }

    @Test
    void shouldRejectPastDueDate() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookIssueService.issueBook(
                        createRequest(LocalDate.now().minusDays(1))
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verifyNoInteractions(
                studentRepository,
                bookRepository,
                bookIssueRepository
        );
    }
    @Test
void shouldReturn404WhenStudentDoesNotExist() {

    when(studentRepository.findById(99))
            .thenReturn(Optional.empty());

    IssueBookRequest request =
            createRequest(LocalDate.now().plusDays(7));

    request.setStudentId(99);

    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> bookIssueService.issueBook(request)
    );

    assertEquals(404, exception.getStatusCode().value());

    verifyNoInteractions(bookRepository, bookIssueRepository);
}

@Test
void shouldReturn404WhenBookDoesNotExist() {

    Student student = createStudent();

    when(studentRepository.findById(1))
            .thenReturn(Optional.of(student));

    when(bookRepository.findById(99))
            .thenReturn(Optional.empty());

    IssueBookRequest request =
            createRequest(LocalDate.now().plusDays(7));

    request.setBookId(99);

    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> bookIssueService.issueBook(request)
    );

    assertEquals(404, exception.getStatusCode().value());

    verify(bookRepository, never()).save(any(Book.class));
    verifyNoInteractions(bookIssueRepository);
}

@Test
void shouldReturn404WhenIssueRecordDoesNotExist() {

    when(bookIssueRepository.findById(999))
            .thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> bookIssueService.returnBook(999)
    );

    assertEquals(404, exception.getStatusCode().value());

    verifyNoInteractions(bookRepository);
    verify(bookIssueRepository, never()).save(any(BookIssue.class));
}

@Test
void shouldReturnAllIssues() {
    Student student = createStudent();
    Book book = createBook(true);

    BookIssue issue = new BookIssue();
    issue.setId(1);
    issue.setStudent(student);
    issue.setBook(book);
    issue.setIssueDate(LocalDate.now());
    issue.setDueDate(LocalDate.now().plusDays(7));

    when(bookIssueRepository.findAllWithStudentAndBook()).thenReturn(List.of(issue));

    List<BookIssueResponse> result = bookIssueService.getAllIssues();

    assertEquals(1, result.size());
    assertEquals("Database Basics", result.get(0).getBookTitle());
}

@Test
void shouldReturnActiveIssues() {
    Student student = createStudent();
    Book book = createBook(false);

    BookIssue issue = new BookIssue();
    issue.setId(1);
    issue.setStudent(student);
    issue.setBook(book);
    issue.setIssueDate(LocalDate.now());
    issue.setDueDate(LocalDate.now().plusDays(7));

    when(bookIssueRepository.findActiveIssuesWithStudentAndBook()).thenReturn(List.of(issue));

    List<BookIssueResponse> result = bookIssueService.getActiveIssues();

    assertEquals(1, result.size());
    assertNull(result.get(0).getReturnDate());
}

@Test
void shouldFindIssueById() {
    Student student = createStudent();
    Book book = createBook(true);

    BookIssue issue = new BookIssue();
    issue.setId(1);
    issue.setStudent(student);
    issue.setBook(book);
    issue.setIssueDate(LocalDate.now());
    issue.setDueDate(LocalDate.now().plusDays(7));

    when(bookIssueRepository.findById(1)).thenReturn(Optional.of(issue));

    BookIssueResponse result = bookIssueService.getIssueById(1);

    assertNotNull(result);
    assertEquals(1, result.getId());
}
}