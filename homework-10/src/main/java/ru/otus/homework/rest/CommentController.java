package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.ObjectNotFoundException;
import ru.otus.homework.service.BookService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final BookService bookService;

    @GetMapping("/api/books/{book_id}/comments")
    public ResponseEntity<List<Comment>> getBookComments(@PathVariable("book_id") String bookId) {
        if (!bookExist(bookId)) {
            return ResponseEntity.notFound().build();
        }

        val commentsList =  bookService.getAllBookComments(bookId);
        val result = commentsList == null ? new ArrayList<Comment>() : commentsList;
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/books/{book_id}/comments/{id}")
    public ResponseEntity<Comment> getBookComment(@PathVariable("book_id") String bookId, @PathVariable("id") String commentId) {
        if (!bookExist(bookId)) {
            return ResponseEntity.notFound().build();
        }

        try {
            val comment = bookService.getComment(bookId, commentId);
            return ResponseEntity.ok(comment);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/books/{book_id}/comments")
    public ResponseEntity<Comment> addBookComment(@PathVariable("book_id") String bookId, @RequestBody Comment comment)
            throws URISyntaxException {
        if (!bookExist(bookId)) {
            return ResponseEntity.notFound().build();
        }

        if (bookService.addComment(bookId, comment)) {
            return ResponseEntity.created(
                    new URI("/api/books/" + bookId + "/comments/" + comment.getId())
            ).body(comment);
        } else {
            throw new RuntimeException();
        }
    }

    @PutMapping("/api/books/{book_id}/comments/{id}")
    public ResponseEntity<Comment> updateBookComment(@PathVariable("book_id") String bookId,
                                            @PathVariable("id") String commentId,
                                            @RequestBody Comment comment) {
        if (comment.getId() == null || !commentId.equals(comment.getId())) {
            return ResponseEntity.badRequest().build();
        }

        if (!bookExist(bookId)) {
            return ResponseEntity.notFound().build();
        }

        if (bookService.updateComment(bookId, comment)) {
            return ResponseEntity.ok(comment);
        } else {
            throw new RuntimeException();
        }
    }

    @DeleteMapping("/api/books/{book_id}/comments/{id}")
    public ResponseEntity<?> deleteBookComment(@PathVariable("book_id") String bookId, @PathVariable("id") String commentId) {
        if (!bookExist(bookId)) {
            return ResponseEntity.notFound().build();
        }

        bookService.deleteComment(bookId, commentId);
        return ResponseEntity.ok().build();
    }

    private boolean bookExist(String bookId) {
        return bookService.existById(bookId);
    }

}
