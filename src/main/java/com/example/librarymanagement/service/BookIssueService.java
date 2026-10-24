package com.example.librarymanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.librarymanagement.dto.BookIssueResponse;
import com.example.librarymanagement.dto.IssueBookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookIssue;
import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.repository.BookIssueRepository;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.StudentRepository;

@Service
public class BookIssueService {

    private final BookIssueRepository bookIssueRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;

    public BookIssueService(
            BookIssueRepository bookIssueRepository,
            BookRepository bookRepository,
            StudentRepository studentRepository) {

        this.bookIssueRepository = bookIssueRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public BookIssueResponse issueBook(IssueBookRequest request) {

        if (request == null
                || request.getStudentId() == null
                || request.getBookId() == null
                || request.getDueDate() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student ID, Book ID and due date are required");
        }

        if (request.getDueDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Due date cannot be in the past");
        }

        Student student = studentRepository
                .findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found with id: " + request.getStudentId()));

        Book book = bookRepository
                .findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found with id: " + request.getBookId()));

        if (!book.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book is not available for issue");
        }

        BookIssue bookIssue = new BookIssue();

        bookIssue.setStudent(student);
        bookIssue.setBook(book);
        bookIssue.setIssueDate(LocalDate.now());
        bookIssue.setDueDate(request.getDueDate());

        book.setAvailable(false);
        bookRepository.save(book);

        BookIssue savedIssue = bookIssueRepository.save(bookIssue);

        return toResponse(savedIssue);
    }

    @Transactional
    public BookIssueResponse returnBook(int issueId) {

        BookIssue bookIssue = bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book issue record not found with id: " + issueId));

        if (bookIssue.getReturnDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book has already been returned");
        }

        bookIssue.setReturnDate(LocalDate.now());

        Book book = bookIssue.getBook();
        book.setAvailable(true);

        bookRepository.save(book);

        BookIssue savedIssue = bookIssueRepository.save(bookIssue);

        return toResponse(savedIssue);
    }

    @Transactional(readOnly = true)
    public List<BookIssueResponse> getAllIssues() {
        return bookIssueRepository.findAllWithStudentAndBook()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookIssueResponse> getActiveIssues() {
        return bookIssueRepository.findActiveIssuesWithStudentAndBook()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookIssueResponse getIssueById(int issueId) {
        BookIssue issue = bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book issue record not found with id: " + issueId));
        return toResponse(issue);
    }

    @Transactional(readOnly = true)
    public List<BookIssueResponse> getIssuesByStudent(int studentId) {
        return bookIssueRepository.findByStudentIdOrderByIdDesc(studentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookIssueResponse> getIssuesByBook(int bookId) {
        return bookIssueRepository.findByBookIdOrderByIdDesc(bookId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BookIssueResponse toResponse(BookIssue bookIssue) {

        return new BookIssueResponse(
                bookIssue.getId(),
                bookIssue.getStudent() != null ? bookIssue.getStudent().getId() : 0,
                bookIssue.getStudent() != null ? bookIssue.getStudent().getName() : "Unknown",
                bookIssue.getBook() != null ? bookIssue.getBook().getId() : 0,
                bookIssue.getBook() != null ? bookIssue.getBook().getTitle() : "Unknown",
                bookIssue.getIssueDate(),
                bookIssue.getDueDate(),
                bookIssue.getReturnDate()
        );
    }
}