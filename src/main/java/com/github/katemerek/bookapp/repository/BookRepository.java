package com.github.katemerek.bookapp.repository;

import com.github.katemerek.bookapp.model.Book;
import com.github.katemerek.bookapp.util.BookNotFoundException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM book", new BeanPropertyRowMapper<>(Book.class));
    }

    public Optional<Book> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM book WHERE id = ?",
                    BeanPropertyRowMapper.newInstance(Book.class), id));
        } catch (BookNotFoundException e) {
            throw new BookNotFoundException(id);
        }
    }

    public Book save(Book book) {
        return jdbcTemplate.queryForObject("INSERT INTO book (title, author, publication_year) values (?, ?, ?) RETURNING id",
                BeanPropertyRowMapper.newInstance(Book.class), book.getTitle(), book.getAuthor(), book.getPublicationYear());
    }

    public void deleteById(Long id) {
        int deletedRows = jdbcTemplate.update("DELETE FROM book WHERE id = ?", id);
        if (deletedRows == 0) {
            throw new BookNotFoundException(id);
        }
    }

    public void update(Book book) {
        int updatedRows = jdbcTemplate.update("UPDATE book SET title = ?, author = ?, publication_year = ? WHERE id = ?", book.getTitle(),
                book.getAuthor(), book.getPublicationYear(), book.getId());
        if (updatedRows == 0) {
            throw new BookNotFoundException(book.getId());
        }
    }
}
