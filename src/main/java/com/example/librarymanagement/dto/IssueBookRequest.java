package com.example.librarymanagement.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class IssueBookRequest {

    @NotNull(message = "Student ID is required")
    private Integer studentId;

    @NotNull(message = "Book ID is required")
    private Integer bookId;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}