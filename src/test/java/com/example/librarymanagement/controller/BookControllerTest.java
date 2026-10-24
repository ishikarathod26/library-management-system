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

import com.example.librarymanagement.dto.BookRequest;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.service.BookService;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private Book createBook(int id, String title, String author, String isbn, boolean available) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setAvailable(available);
        return book;
    }

    @Test
    void getAllBooks_shouldReturnBooks() throws Exception {
        Book book = createBook(1, "Test Book", "Ishika", "TEST001", true);

        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Ishika"));
    }

    @Test
    void getBookById_whenExists_shouldReturnBook() throws Exception {
        Book book = createBook(1, "Test Book", "Ishika", "TEST001", true);

        when(bookService.getBookById(1)).thenReturn(book);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_whenNotFound_shouldReturn404() throws Exception {
        when(bookService.getBookById(99)).thenReturn(null);

        mockMvc.perform(get("/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_withValidData_shouldReturn201() throws Exception {
        Book savedBook = createBook(1, "Clean Code", "Robert Martin", "ISBN999", true);

        when(bookService.createBook(any(BookRequest.class))).thenReturn(savedBook);

        String json = """
                {
                    "title": "Clean Code",
                    "author": "Robert Martin",
                    "isbn": "ISBN999",
                    "available": true
                }
                """;

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void updateBook_whenExists_shouldReturnUpdatedBook() throws Exception {
        Book updatedBook = createBook(1, "Clean Code 2nd Ed", "Robert Martin", "ISBN999", true);

        when(bookService.updateBook(eq(1), any(Book.class))).thenReturn(updatedBook);

        String json = """
                {
                    "title": "Clean Code 2nd Ed",
                    "author": "Robert Martin",
                    "isbn": "ISBN999",
                    "available": true
                }
                """;

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code 2nd Ed"));
    }

    @Test
    void updateBook_whenNotFound_shouldReturn404() throws Exception {
        when(bookService.updateBook(eq(99), any(Book.class))).thenReturn(null);

        String json = """
                {
                    "title": "Non-existent",
                    "author": "Author",
                    "isbn": "ISBN000",
                    "available": true
                }
                """;

        mockMvc.perform(put("/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_shouldReturnSuccess() throws Exception {
        doNothing().when(bookService).deleteBook(1);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));
    }
}