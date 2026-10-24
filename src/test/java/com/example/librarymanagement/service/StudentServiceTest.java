package com.example.librarymanagement.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private com.example.librarymanagement.repository.BookIssueRepository bookIssueRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, bookIssueRepository);
    }

    private Student createStudent(int id, String name, String email) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setEmail(email);
        return student;
    }

    @Test
    void shouldCreateStudent() {
        Student student = createStudent(
                1, "Ishika", "ishika@gmail.com");

        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.createStudent(student);

        assertEquals(1, result.getId());
        assertEquals("Ishika", result.getName());
        verify(studentRepository).save(student);
    }

    @Test
    void shouldReturnAllStudents() {
        List<Student> students = List.of(
                createStudent(1, "Ishika", "ishika@gmail.com"),
                createStudent(2, "Rahul", "rahul@gmail.com"));

        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentService.getAllStudents();

        assertEquals(2, result.size());
        verify(studentRepository).findAll();
    }

    @Test
    void shouldFindStudentById() {
        Student student = createStudent(
                1, "Ishika", "ishika@gmail.com");

        when(studentRepository.findById(1))
                .thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1);

        assertNotNull(result);
        assertEquals("Ishika", result.getName());
    }

    @Test
    void shouldReturnNullWhenStudentDoesNotExist() {
        when(studentRepository.findById(99))
                .thenReturn(Optional.empty());

        Student result = studentService.getStudentById(99);

        assertNull(result);
    }

    @Test
    void shouldUpdateExistingStudent() {
        Student existing = createStudent(
                1, "Old Name", "old@gmail.com");

        Student updates = createStudent(
                1, "New Name", "new@gmail.com");

        when(studentRepository.findById(1))
                .thenReturn(Optional.of(existing));

        when(studentRepository.save(existing))
                .thenReturn(existing);

        Student result = studentService.updateStudent(1, updates);

        assertEquals("New Name", result.getName());
        assertEquals("new@gmail.com", result.getEmail());
        verify(studentRepository).save(existing);
    }

    @Test
    void shouldNotUpdateMissingStudent() {
        Student updates = createStudent(
                99, "New Name", "new@gmail.com");

        when(studentRepository.findById(99))
                .thenReturn(Optional.empty());

        Student result = studentService.updateStudent(99, updates);

        assertNull(result);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void shouldDeleteStudentById() {
        when(bookIssueRepository.existsByStudentIdAndReturnDateIsNull(1)).thenReturn(false);

        studentService.deleteStudent(1);

        verify(studentRepository).deleteById(1);
    }

    @Test
    void shouldThrowConflictWhenDeletingStudentWithActiveIssues() {
        when(bookIssueRepository.existsByStudentIdAndReturnDateIsNull(1)).thenReturn(true);

        org.springframework.web.server.ResponseStatusException ex =
                assertThrows(org.springframework.web.server.ResponseStatusException.class,
                        () -> studentService.deleteStudent(1));

        assertEquals(409, ex.getStatusCode().value());
        verify(studentRepository, never()).deleteById(1);
    }

    @Test
    void shouldSearchStudents() {
        List<Student> students = List.of(createStudent(1, "Ishika", "ishika@gmail.com"));
        when(studentRepository.searchStudents("Ishika")).thenReturn(students);

        List<Student> result = studentService.searchStudents("Ishika");

        assertEquals(1, result.size());
        assertEquals("Ishika", result.get(0).getName());
    }
}