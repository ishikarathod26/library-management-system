package com.example.librarymanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.librarymanagement.entity.Student;
import com.example.librarymanagement.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    private Student createStudent(int id, String name, String email) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setEmail(email);
        return student;
    }

    @Test
    void getAllStudents_shouldReturnList() throws Exception {
        Student s1 = createStudent(1, "Ishika", "ishika@gmail.com");
        Student s2 = createStudent(2, "Rahul", "rahul@gmail.com");

        when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ishika"))
                .andExpect(jsonPath("$[1].name").value("Rahul"));
    }

    @Test
    void getStudentById_whenExists_shouldReturnStudent() throws Exception {
        Student s = createStudent(1, "Ishika", "ishika@gmail.com");

        when(studentService.getStudentById(1)).thenReturn(s);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ishika"))
                .andExpect(jsonPath("$.email").value("ishika@gmail.com"));
    }

    @Test
    void getStudentById_whenNotFound_shouldReturn404() throws Exception {
        when(studentService.getStudentById(99)).thenReturn(null);

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent_withValidData_shouldReturn201() throws Exception {
        Student saved = createStudent(1, "Ishika", "ishika@gmail.com");

        when(studentService.createStudent(any(Student.class))).thenReturn(saved);

        String json = """
                {
                    "name": "Ishika",
                    "email": "ishika@gmail.com"
                }
                """;

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ishika"))
                .andExpect(jsonPath("$.email").value("ishika@gmail.com"));
    }

    @Test
    void updateStudent_whenExists_shouldReturnUpdated() throws Exception {
        Student updated = createStudent(1, "Ishika Updated", "ishika.updated@gmail.com");

        when(studentService.updateStudent(eq(1), any(Student.class))).thenReturn(updated);

        String json = """
                {
                    "name": "Ishika Updated",
                    "email": "ishika.updated@gmail.com"
                }
                """;

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ishika Updated"));
    }

    @Test
    void updateStudent_whenNotFound_shouldReturn404() throws Exception {
        when(studentService.updateStudent(eq(99), any(Student.class))).thenReturn(null);

        String json = """
                {
                    "name": "Ghost",
                    "email": "ghost@gmail.com"
                }
                """;

        mockMvc.perform(put("/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_shouldReturnSuccess() throws Exception {
        doNothing().when(studentService).deleteStudent(1);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Student deleted successfully"));
    }
}
