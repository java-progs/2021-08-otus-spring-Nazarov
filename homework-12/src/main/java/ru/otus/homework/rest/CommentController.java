package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.service.BookService;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final BookService bookService;

    @PostMapping(value = "/addComment", params = {"bookId", "name", "commentText"})
    public String addComment(@RequestParam("bookId") String bookId,
                             @RequestParam("name") String author,
                             @RequestParam("commentText") String text,
                             Model model) {
        val comment = new Comment(author, text);
        bookService.addComment(bookId, comment);
        fillBookModel(bookId, model);
        return "bookDetails";
    }

    @GetMapping(value = "/updateComment", params = {"bookId", "commentId"})
    public String updateComment(@RequestParam("bookId") String bookId,
                                @RequestParam("commentId") String commentId,
                                Model model) {
        fillBookModel(bookId, model);
        if (model.getAttribute("error") != null) {
            return "bookDetails";
        }

        val comment = bookService.getComment(bookId, commentId);
        if (comment == null) {
            return "bookDetails";
        }

        model.addAttribute("action", "updateComment");
        model.addAttribute("comment", comment);
        return "bookDetails";
    }

    @PostMapping(value = "/updateComment", params = {"bookId", "commentId", "commentText"})
    public String updateComment(@RequestParam("bookId") String bookId,
                                @RequestParam("commentId") String commentId,
                                @RequestParam("commentText") String text,
                                Model model) {
        bookService.updateComment(bookId, commentId, text);
        fillBookModel(bookId, model);
        return "bookDetails";
    }

    @GetMapping(value = "/deleteComment")
    public String deleteComment(@RequestParam("bookId") String bookId,
                                @RequestParam("commentId") String commentId,
                                Model model) {
        bookService.deleteComment(bookId, commentId);
        fillBookModel(bookId, model);
        return "bookDetails";
    }

    private void fillBookModel(String bookId, Model model) {
        try {
            val book = bookService.getBookById(bookId);
            model.addAttribute("book", book);
        } catch (Exception e) {
            model.addAttribute("error", "Book not found");
        }
    }

}
