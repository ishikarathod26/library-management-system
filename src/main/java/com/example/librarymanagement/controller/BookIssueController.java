package com.example.librarymanagement.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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