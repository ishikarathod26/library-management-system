package com.example.librarymanagement.service;
import java.util.List;
import org.springframework.stereotype.Service;

import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }
    public List<Student> getAllStudents() {
    return studentRepository.findAll();
}
}