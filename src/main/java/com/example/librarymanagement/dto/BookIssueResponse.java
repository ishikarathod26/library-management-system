
package com.example.librarymanagement.dto;

import java.time.LocalDate;

public class BookIssueResponse {

    private final int id;
    private final int studentId;
    private final String studentName;
    private final int bookId;
    private final String bookTitle;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;

    public BookIssueResponse(
            int id,
            int studentId,
            String studentName,
            int bookId,
            String bookTitle,
            LocalDate issueDate,
            LocalDate dueDate,
            LocalDate returnDate) {

        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
}