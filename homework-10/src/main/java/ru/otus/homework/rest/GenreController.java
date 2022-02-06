package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.service.GenreService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAll() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public ResponseEntity getGenre(@PathVariable String id) {
        try {
            val genre = genreService.getGenreById(id);
            return ResponseEntity.ok(genre);
        } catch (RecordNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity addGenre(@RequestBody Genre genre) throws URISyntaxException {
        val newGenre = genreService.saveGenre(genre);
        if (newGenre != null) {
            return ResponseEntity.created(new URI("/api/genres/" + newGenre.getId())).body(newGenre);
        } else {
            throw new RuntimeException();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateGenre(@PathVariable("id") String id, @RequestBody Genre genre) {
        if (genre.getId() == null || !id.equals(genre.getId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            genreService.getGenreById(id);
        } catch (RecordNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if (genreService.updateGenre(genre)) {
            return ResponseEntity.ok(genre);
        } else {
            throw new RuntimeException();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGenre(@PathVariable("id") String id) {
        try {
            genreService.deleteGenreById(id);
            return ResponseEntity.ok().build();
        } catch (ViolationOfConstraintException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
