package com.example.librarymanagement.service;

import java.time.LocalDate;

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
                        "Student not found"));

        Book book = bookRepository
                .findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"));

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
                        "Book issue record not found"));

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

    private BookIssueResponse toResponse(BookIssue bookIssue) {

        return new BookIssueResponse(
                bookIssue.getId(),
                bookIssue.getStudent().getId(),
                bookIssue.getStudent().getName(),
                bookIssue.getBook().getId(),
                bookIssue.getBook().getTitle(),
                bookIssue.getIssueDate(),
                bookIssue.getDueDate(),
                bookIssue.getReturnDate()
        );
    }
}