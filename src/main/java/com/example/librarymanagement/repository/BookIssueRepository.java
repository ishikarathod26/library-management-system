package com.example.librarymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.librarymanagement.entity.BookIssue;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Integer> {

    @Query("SELECT b FROM BookIssue b JOIN FETCH b.student JOIN FETCH b.book ORDER BY b.id DESC")
    List<BookIssue> findAllWithStudentAndBook();

    @Query("SELECT b FROM BookIssue b JOIN FETCH b.student JOIN FETCH b.book WHERE b.returnDate IS NULL ORDER BY b.dueDate ASC")
    List<BookIssue> findActiveIssuesWithStudentAndBook();

    List<BookIssue> findByStudentIdOrderByIdDesc(int studentId);

    List<BookIssue> findByBookIdOrderByIdDesc(int bookId);

    boolean existsByBookIdAndReturnDateIsNull(int bookId);

    boolean existsByStudentIdAndReturnDateIsNull(int studentId);
}