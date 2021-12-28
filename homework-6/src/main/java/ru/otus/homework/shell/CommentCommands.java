package ru.otus.homework.shell;

import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.CommentService;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

import java.util.stream.Collectors;

@ShellComponent
public class CommentCommands {

    private final CommentService commentService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public CommentCommands(CommentService commentService, ShellHelper shellHelper, InputReader inputReader) {
        this.commentService = commentService;
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
    }

    @ShellMethod(value = "show comments list", key = {"list-comments"})
    public String getCommentsList() {
        val commentsList = commentService.getAllComments();
        return String.format("Comments list:%n%s",
                commentsList.stream().map(c -> getCommentDescription(c)).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get comment by id", key = {"get-comment"})
    public String getCommentById(Long id) {
        Comment comment;
        try {
            comment = commentService.getCommentById(id);
        } catch (RecordNotFoundException e) {
            return shellHelper.getInfoMessage("Comment not found");
        }

        return String.format("Comment: %s", getCommentDescription(comment));
    }

    @ShellMethod(value = "add comment", key = {"add-comment"})
    public String addComment() {
        String author;
        String text;
        long bookId = -1;

        do {
            try {
                bookId = Long.parseLong(inputReader.prompt("Book id"));
            } catch (NumberFormatException e) {
                shellHelper.printError("Invalid id value");
            }
        } while (bookId < 1);

        do {
            author = inputReader.prompt("User name");
        } while (!StringUtils.hasText(author));

        do {
            text = inputReader.prompt("Text");
        } while (!StringUtils.hasText(text));

        if (commentService.saveComment(author, text, bookId)) {
            return shellHelper.getSuccessMessage("Comment added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding comment");
        }
    }

    @ShellMethod(value = "delete comment", key = {"delete-comment"})
    public String deleteComment(long id) {
        if (commentService.deleteCommentById(id)) {
            return shellHelper.getSuccessMessage("Comment deleted successful");
        } else {
            return shellHelper.getErrorMessage("Comment deleting genre");
        }
    }

    @ShellMethod(value = "update comment", key = {"update-comment"})
    public String updateComment() {
        long id = -1;
        String text;

        do {
            try {
                id = Long.parseLong(inputReader.prompt("Id of the comment being updated"));
            } catch (NumberFormatException e) {
                shellHelper.printError("Invalid id value");
                id = -1;
            }
        } while (id < 1);

        do {
            text = inputReader.prompt("Text");
        } while (!StringUtils.hasText(text));

        if (commentService.updateComment(id, text)) {
            return shellHelper.getSuccessMessage("Comment updated successful");
        } else {
            return shellHelper.getErrorMessage("Error updating comment");
        }
    }

    public String getCommentDescription(Comment comment) {
        val description = new StringBuilder();
        description.append(String.format("Id: %s, Date: %s, Username: %s, Text: %s%n",
                comment.getId(), shellHelper.getFormatTime(comment.getTime()), comment.getAuthor(), comment.getText()));

        return description.toString();
    }
}
