package com.example.librarymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.librarymanagement.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}