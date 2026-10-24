package com.example.librarymanagement.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.librarymanagement.dto.BookIssueResponse;
import com.example.librarymanagement.dto.IssueBookRequest;
import com.example.librarymanagement.service.BookIssueService;

@RestController
@RequestMapping("/book-issues")
public class BookIssueController {

    private final BookIssueService bookIssueService;

    public BookIssueController(BookIssueService bookIssueService) {
        this.bookIssueService = bookIssueService;
    }

    // Get all book issues (optional status=active filter)
    @GetMapping
    public List<BookIssueResponse> getAllIssues(
            @RequestParam(required = false) String status) {
        if ("active".equalsIgnoreCase(status)) {
            return bookIssueService.getActiveIssues();
        }
        return bookIssueService.getAllIssues();
    }

    // Get active book issues
    @GetMapping("/active")
    public List<BookIssueResponse> getActiveIssues() {
        return bookIssueService.getActiveIssues();
    }

    // Get single book issue by ID
    @GetMapping("/{issueId}")
    public ResponseEntity<BookIssueResponse> getIssueById(@PathVariable int issueId) {
        BookIssueResponse response = bookIssueService.getIssueById(issueId);
        return ResponseEntity.ok(response);
    }

    // Get book issues for a student
    @GetMapping("/student/{studentId}")
    public List<BookIssueResponse> getIssuesByStudent(@PathVariable int studentId) {
        return bookIssueService.getIssuesByStudent(studentId);
    }

    // Get book issues for a book
    @GetMapping("/book/{bookId}")
    public List<BookIssueResponse> getIssuesByBook(@PathVariable int bookId) {
        return bookIssueService.getIssuesByBook(bookId);
    }

    // Issue a book
    @PostMapping
    public ResponseEntity<BookIssueResponse> issueBook(
            @Valid @RequestBody IssueBookRequest request) {

        BookIssueResponse response = bookIssueService.issueBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Return a book
    @PutMapping("/{issueId}/return")
    public ResponseEntity<BookIssueResponse> returnBook(
            @PathVariable int issueId) {

        BookIssueResponse response =
                bookIssueService.returnBook(issueId);

        return ResponseEntity.ok(response);
    }
}