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
public Student getStudentById(int id){
    return studentRepository.findById(id).orElse(null);
}
public Student updateStudent(int id, Student student){
    Student existingStudent= studentRepository.findById(id).orElse(null);
    if(existingStudent == null){
        return null;
    }
    existingStudent.setName(student.getName());
    existingStudent.setEmail(student.getEmail());
    return  studentRepository.save(existingStudent);
}
public  void deleteStudent(int id){
    studentRepository.deleteById(id);
}
}