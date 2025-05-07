package com.github.katemerek.bookapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.katemerek.bookapp.model.Book;
import com.github.katemerek.bookapp.service.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private BookServiceImpl bookService;
    @Autowired
    ObjectMapper mapper;

    private List<Book> createBooks() {
        Book one = new Book();
        Book two = new Book();
        return List.of(one, two);
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPublicationYear(2020);
        return book;
    }

    private Book createInvalidBook() {
        Book book = new Book();
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPublicationYear(1000);
        return book;
    }


    @Test
    void getAllBooks_ShouldReturnAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(createBooks());

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        Mockito.when(bookService.getBookById(1L))
                .thenReturn(Optional.of(createBook()));
        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title"));

    }

    @Test
    void getBookById_shouldReturnNotFound() throws Exception {
        Mockito.when(bookService.getBookById(anyLong()))
                .thenReturn(Optional.empty());

        mvc.perform(get("/api/books/767"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_ShouldReturnOk() throws Exception {
        when(bookService.saveBook(any(Book.class))).thenReturn(createBook());

        mvc.perform(post("/api/books")
                        .content(mapper.writeValueAsString(createBook()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    void createBook_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(bookService.saveBook(any(Book.class))).thenReturn(createInvalidBook());

        mvc.perform(post("/api/books")
                        .content(mapper.writeValueAsString(createInvalidBook()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook_shouldUpdateBook() throws Exception {

        mvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createBook())))
                .andExpect(status().isOk());
    }

    @Test
    void updateBook_shouldValidateInput() throws Exception {

        mvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createInvalidBook())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBook_shouldReturnOk() throws Exception {
        mvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk());

        Mockito.verify(bookService).deleteBookById(1L);
    }
}
