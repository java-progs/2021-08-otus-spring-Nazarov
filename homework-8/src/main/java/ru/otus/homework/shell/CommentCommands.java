package ru.otus.homework.shell;

import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.service.BookService;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

import java.util.stream.Collectors;

@ShellComponent
public class CommentCommands {

    private final BookService bookService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public CommentCommands(BookService bookService, ShellHelper shellHelper, InputReader inputReader) {
        this.bookService = bookService;
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
    }

    @ShellMethod(value = "show book comments list", key = {"list-comments"})
    public String getBookComments(String bookId) {
        val commentsList = bookService.getAllBookComments(bookId);
        return String.format("Comments list:%n%s",
                commentsList.stream().map(c -> shellHelper.getCommentDescription(c)).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get comment by id", key = {"get-comment"})
    public String getCommentById() {
        String bookId;
        String commentId;
        Comment comment;

        do {
            bookId = inputReader.prompt("Book id");
        } while (!StringUtils.hasText(bookId));

        do {
            commentId = inputReader.prompt("Comment id");
        } while (!StringUtils.hasText(commentId));

        try {
            comment = bookService.getComment(bookId, commentId);
        } catch (Exception e) {
            return shellHelper.getInfoMessage("Comment not found");
        }

        return String.format("Comment: %s", shellHelper.getCommentDescription(comment));
    }

    @ShellMethod(value = "add comment", key = {"add-comment"})
    public String addComment() {
        String author;
        String text;
        String bookId;

        do {
            bookId = inputReader.prompt("Book id");
        } while (!StringUtils.hasText(bookId));

        do {
            author = inputReader.prompt("User name");
        } while (!StringUtils.hasText(author));

        do {
            text = inputReader.prompt("Text");
        } while (!StringUtils.hasText(text));

        val comment = new Comment(author, text);
        if (bookService.addComment(bookId, comment)) {
            return shellHelper.getSuccessMessage("Comment added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding comment");
        }
    }

    @ShellMethod(value = "delete comment", key = {"delete-comment"})
    public String deleteComment() {
        String bookId;
        String commentId;

        do {
            bookId = inputReader.prompt("Book id");
        } while (!StringUtils.hasText(bookId));

        do {
            commentId = inputReader.prompt("Comment id");
        } while (!StringUtils.hasText(commentId));

        try {
            bookService.deleteComment(bookId, commentId);
            return shellHelper.getSuccessMessage("Comment deleted successful");
        } catch (Exception e) {
            return shellHelper.getErrorMessage("Comment deleting comment");
        }
    }

    @ShellMethod(value = "update comment", key = {"update-comment"})
    public String updateComment() {
        String bookId;
        String commentId;
        String text;

        do {
            bookId = inputReader.prompt("Book id");
        } while (!StringUtils.hasText(bookId));

        do {
            commentId = inputReader.prompt("Comment id");
        } while (!StringUtils.hasText(commentId));

        do {
            text = inputReader.prompt("Text");
        } while (!StringUtils.hasText(text));

        if (bookService.updateComment(bookId, commentId, text)) {
            return shellHelper.getSuccessMessage("Comment updated successful");
        } else {
            return shellHelper.getErrorMessage("Error updating comment");
        }
    }

}
