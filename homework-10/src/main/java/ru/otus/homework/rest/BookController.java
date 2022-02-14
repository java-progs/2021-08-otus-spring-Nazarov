package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.exception.ObjectNotFoundException;
import ru.otus.homework.exception.RestException;
import ru.otus.homework.service.BookService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final Mapper mapper;

    @GetMapping("/api/books")
    public List<BookDto> getAll() {
        return bookService.getAllBooks().stream().map(b -> mapper.toDto(b)).collect(Collectors.toList());
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable("id") String id) {
        try {
            val bookDto =  mapper.toDto(bookService.getBookById(id));
            return ResponseEntity.ok(bookDto);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/books")
    public ResponseEntity<BookDto> addBook(@RequestBody BookDto bookDto) throws URISyntaxException {
        val book = mapper.toBook(bookDto);
        val newBook = bookService.saveBook(book);
        if (newBook != null) {
            return ResponseEntity.created(new URI("/api/books/" + newBook.getId())).body(mapper.toDto(newBook));
        } else {
            throw new RestException("Adding book error");
        }
    }

    @PutMapping("/api/books/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable("id") String id, @RequestBody BookDto bookDto) {
        if (bookDto.getId() == null || !id.equals(bookDto.getId())) {
            return ResponseEntity.badRequest().build();
        }

        if (!bookService.existById(id)) {
            return ResponseEntity.notFound().build();
        }

        val book = mapper.toBook(bookDto);

        if (bookService.updateBook(book)) {
            return ResponseEntity.ok(mapper.toDto(book));
        } else {
            throw new RestException("Updating book error");
        }
    }

    @DeleteMapping("/api/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") String id) {
        bookService.deleteBookById(id);
        return ResponseEntity.ok().build();
    }

}
