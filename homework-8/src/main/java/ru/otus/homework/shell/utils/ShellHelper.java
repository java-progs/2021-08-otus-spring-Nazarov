package ru.otus.homework.shell.utils;

import lombok.val;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ShellHelper {

    public final String infoColor;

    public final String successColor;

    public final String warningColor;

    public final String errorColor;

    private final Terminal terminal;

    public ShellHelper(Terminal terminal, String infoColor, String successColor, String warningColor, String errorColor) {
        this.terminal = terminal;
        this.infoColor = infoColor;
        this.successColor = successColor;
        this.warningColor = warningColor;
        this.errorColor = errorColor;
    }

    public String getColored(String message, PromptColor color) {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        String coloredString = builder.append(message, AttributedStyle.DEFAULT
                .foreground(color.toJlineAttributedStyle()))
                .toAnsi();

        return coloredString;
    }

    public String getInfoMessage(String message) {
        return getColored(message, PromptColor.valueOf(infoColor));
    }

    public String getSuccessMessage(String message) {
        return getColored(message, PromptColor.valueOf(successColor));
    }

    public String getWarningMessage(String message) {
        return getColored(message, PromptColor.valueOf(warningColor));
    }

    public String getErrorMessage(String message) {
        return getColored(message, PromptColor.valueOf(errorColor));
    }

    public void printInfo(String message) {
        print(getInfoMessage(message));
    }

    public void printSuccess(String message) {
        print(getSuccessMessage(message));
    }

    public void printWarning(String message) {
        print(getWarningMessage(message));
    }

    public void printError(String message) {
        print(getErrorMessage(message));
    }

    public void print(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }

    public String getAuthorDescription(Author author) {
        var description = new StringBuilder();

        description.append(String.format("   Id: %s, Surname: %s, Name: %s", author.getId(), author.getSurname(), author.getName()));

        if (author.getPatronymic() != null) {
            description.append(String.format(", Patronymic: %s", author.getPatronymic()));
        }

        description.append(String.format("%n"));

        return description.toString();
    }

    public String getBookDescription(Book book, List<Author> authorsList, List<Genre> genresList) {
        val description = new StringBuilder();

        description.append(String.format("Id: %s%n", book.getId()));
        description.append(String.format("Name: %s%n", book.getName()));
        description.append(String.format("ISBN: %s%n", book.getIsbn() == null ? "-" : book.getIsbn()));
        description.append(String.format("Authors:%n"));

        for (Author a : authorsList) {
            description.append(String.format("   Surname: %s, Name: %s", a.getSurname(), a.getName()));

            if (a.getPatronymic() != null) {
                description.append(String.format(", Patronymic: %s", a.getPatronymic()));
            }

            description.append(String.format("%n"));
        }

        description.append(String.format("Genres:%n"));

        for (Genre g : genresList) {
            description.append(String.format("   Name: %s%n", g.getName()));
        }
        val bookComments = book.getCommentsList();

        if(bookComments != null && bookComments.size() > 0) {
            description.append(String.format("Comments:%n"));

            for (Comment c : bookComments) {
                description.append(String.format("   Id: %s, Date: %s, Author: %s, Text: %s%n", c.getId(), getFormatTime(c.getTime()), c.getAuthor(), c.getText()));
            }
        }

        return description.toString();
    }

    public String getCommentDescription(Comment comment) {
        val description = new StringBuilder();
        description.append(String.format("Id: %s, Date: %s, Username: %s, Text: %s%n",
                comment.getId(), getFormatTime(comment.getTime()), comment.getAuthor(), comment.getText()));

        return description.toString();
    }

    public String getGenreDescription(Genre genre) {
        val description = new StringBuilder();

        description.append(String.format("Id: %s, Name:%s%n", genre.getId(), genre.getName()));

        return description.toString();
    }

    public String getFormatTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm");
        return time.format(formatter);
    }
}
