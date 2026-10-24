package com.example.librarymanagement.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.repository.BookIssueRepository;
import com.example.librarymanagement.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final BookIssueRepository bookIssueRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public StudentService(StudentRepository studentRepository, BookIssueRepository bookIssueRepository) {
        this.studentRepository = studentRepository;
        this.bookIssueRepository = bookIssueRepository;
    }

    public StudentService(StudentRepository studentRepository) {
        this(studentRepository, null);
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(int id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(int id, Student student) {
        Student existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            return null;
        }
        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(int id) {
        if (bookIssueRepository != null && bookIssueRepository.existsByStudentIdAndReturnDateIsNull(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete student with active borrowed books");
        }
        studentRepository.deleteById(id);
    }

    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentRepository.searchStudents(query.trim());
    }
}