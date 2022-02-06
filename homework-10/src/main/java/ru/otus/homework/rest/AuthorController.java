package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.service.AuthorService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<Author> getAll(Model model) {
        return authorService.getAllAuthors();
    }

    @GetMapping("/{id}")
    public ResponseEntity getAuthor(@PathVariable String id) {
        try {
            val author = authorService.getAuthorById(id);
            return ResponseEntity.ok(author);
        } catch (RecordNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity addAuthor(@RequestBody Author author) throws URISyntaxException {
        val newAuthor = authorService.saveAuthor(author);
        if (newAuthor != null) {
            return ResponseEntity.created(new URI("/api/authors/" + newAuthor.getId())).body(newAuthor);
        } else {
            throw new RuntimeException();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAuthor(@PathVariable("id") String id, @RequestBody Author author) {
        if (author.getId() == null || !author.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            authorService.getAuthorById(id);
        } catch (RecordNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if (authorService.updateAuthor(author)) {
            return ResponseEntity.ok(author);
        } else {
            throw new RuntimeException();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAuthor(@PathVariable String id) {
        try {
            authorService.deleteAuthorById(id);
            return ResponseEntity.ok().build();
        } catch (ViolationOfConstraintException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
