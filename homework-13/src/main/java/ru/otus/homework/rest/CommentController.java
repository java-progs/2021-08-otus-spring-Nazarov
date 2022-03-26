package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.BookService;
import ru.otus.homework.service.CommentService;

import java.sql.Timestamp;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final BookService bookService;
    private final CommentService commentService;

    @PostMapping(value = "/addComment", params = {"bookId", "commentText"})
    public String addComment(@RequestParam("bookId") long bookId,
                             @RequestParam("commentText") String text,
                             Model model) {
        try {
            val book = bookService.getBookById(bookId);
            var author = SecurityContextHolder.getContext().getAuthentication().getName();
            if (author == null) {
                author = "anonymous";
            }
            val comment = new Comment(0, author, new Timestamp(System.currentTimeMillis()), text, book);
            commentService.saveComment(comment);
        } catch (RecordNotFoundException e) {
        }

        fillBookModel(bookId, model);
        return "bookDetails";
    }

    @GetMapping(value = "/updateComment", params = {"bookId", "commentId"})
    public String updateComment(@RequestParam("bookId") long bookId,
                                @RequestParam("commentId") long commentId,
                                Model model) {
        try {
            val comment = commentService.getCommentById(commentId);
            model.addAttribute("action", "updateComment");
            model.addAttribute("comment", comment);
        } catch (RecordNotFoundException e) {
        }

        fillBookModel(bookId, model);

        return "bookDetails";
    }

    @PostMapping(value = "/updateComment", params = {"bookId", "commentId", "commentText"})
    public String updateComment(@RequestParam("bookId") long bookId,
                                @RequestParam("commentId") long commentId,
                                @RequestParam("commentText") String text,
                                Model model) {
        try {
            val comment = commentService.getCommentById(commentId);
            comment.setText(text);
            commentService.updateComment(comment);
        } catch (RecordNotFoundException e) {
        }

        fillBookModel(bookId, model);
        return "bookDetails";
    }

    @GetMapping(value = "/deleteComment")
    public String deleteComment(@RequestParam("bookId") long bookId,
                                @RequestParam("commentId") long commentId,
                                Model model) {
        try {
            val comment = commentService.getCommentById(commentId);
            commentService.deleteComment(comment);
        } catch (Exception e) {
        }

        fillBookModel(bookId, model);
        return "bookDetails";
    }

    private void fillBookModel(long bookId, Model model) {
        try {
            val book = bookService.getBookById(bookId);
            val bookComments = commentService.getBookComments(bookId);
            model.addAttribute("book", book);
            model.addAttribute("comments", bookComments);
        } catch (Exception e) {
            model.addAttribute("error", "Book not found");
        }
    }

}
