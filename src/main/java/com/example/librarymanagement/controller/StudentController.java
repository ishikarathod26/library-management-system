package com.example.librarymanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.service.StudentService;

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // CREATE Student
    @PostMapping("/students")
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    // READ All Students
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // READ Student By ID
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {

        Student student = studentService.getStudentById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    // UPDATE Student
    @PutMapping("/students/{id}")
    public Student updateStudent(
            @PathVariable int id,
            @RequestBody Student student) {

        return studentService.updateStudent(id, student);
    }

    // DELETE Student
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {

        studentService.deleteStudent(id);

        return ResponseEntity.noContent().build();
    }
}