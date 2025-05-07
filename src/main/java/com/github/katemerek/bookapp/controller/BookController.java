package com.github.katemerek.bookapp.controller;

import com.github.katemerek.bookapp.model.Book;
import com.github.katemerek.bookapp.responce.BookIdResponse;
import com.github.katemerek.bookapp.service.BookServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookServiceImpl bookService;

    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookIdResponse> createBook(@RequestBody @Valid Book book) {
        Book bookToAdd = bookService.saveBook(book);
        BookIdResponse bookIdResponse = new BookIdResponse(bookToAdd.getId());
        return ResponseEntity.ok(bookIdResponse);
    }

    @DeleteMapping("/{id}")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
    }

    @PutMapping("/{id}")
    public void updateBook(@PathVariable Long id, @RequestBody @Valid Book book) {
        bookService.updateBook(book);
    }
}
