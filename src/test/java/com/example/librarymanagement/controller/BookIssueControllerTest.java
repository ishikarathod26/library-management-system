package com.example.librarymanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.librarymanagement.dto.BookIssueResponse;
import com.example.librarymanagement.dto.IssueBookRequest;
import com.example.librarymanagement.service.BookIssueService;

@WebMvcTest(BookIssueController.class)
class BookIssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookIssueService bookIssueService;

    private BookIssueResponse createResponse(int id, int studentId, String studentName, int bookId, String bookTitle, LocalDate returnDate) {
        return new BookIssueResponse(
                id,
                studentId,
                studentName,
                bookId,
                bookTitle,
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                returnDate
        );
    }

    @Test
    void getAllIssues_shouldReturnList() throws Exception {
        BookIssueResponse res = createResponse(1, 1, "Ishika", 2, "Spring Boot Basics", null);

        when(bookIssueService.getAllIssues()).thenReturn(List.of(res));

        mockMvc.perform(get("/book-issues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("Spring Boot Basics"))
                .andExpect(jsonPath("$[0].studentName").value("Ishika"));
    }

    @Test
    void getActiveIssues_shouldReturnActiveList() throws Exception {
        BookIssueResponse res = createResponse(1, 1, "Ishika", 2, "Spring Boot Basics", null);

        when(bookIssueService.getActiveIssues()).thenReturn(List.of(res));

        mockMvc.perform(get("/book-issues/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].returnDate").doesNotExist());
    }

    @Test
    void getIssueById_whenExists_shouldReturnIssue() throws Exception {
        BookIssueResponse res = createResponse(1, 1, "Ishika", 2, "Spring Boot Basics", null);

        when(bookIssueService.getIssueById(1)).thenReturn(res);

        mockMvc.perform(get("/book-issues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentName").value("Ishika"));
    }

    @Test
    void issueBook_withValidRequest_shouldReturn201() throws Exception {
        BookIssueResponse res = createResponse(1, 1, "Ishika", 2, "Spring Boot Basics", null);

        when(bookIssueService.issueBook(any(IssueBookRequest.class))).thenReturn(res);

        String json = """
                {
                    "studentId": 1,
                    "bookId": 2,
                    "dueDate": "2026-11-01"
                }
                """;

        mockMvc.perform(post("/book-issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookTitle").value("Spring Boot Basics"));
    }

    @Test
    void returnBook_whenValid_shouldReturn200() throws Exception {
        BookIssueResponse res = createResponse(1, 1, "Ishika", 2, "Spring Boot Basics", LocalDate.now());

        when(bookIssueService.returnBook(eq(1))).thenReturn(res);

        mockMvc.perform(put("/book-issues/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnDate").value(LocalDate.now().toString()));
    }
}
