package ru.otus.homework.shell;

import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

import java.util.stream.Collectors;

@ShellComponent
public class AuthorsCommands {

    private final AuthorService authorService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public AuthorsCommands(ShellHelper shellHelper, InputReader inputReader, AuthorService authorService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.authorService = authorService;
    }

    @ShellMethod(value = "show authors list", key = {"list-authors"})
    public String getAuthorsList() {
        val authorsList = authorService.getAllAuthors();
        return String.format("Authors list:%n%s",
                authorsList.stream().map(Author::toString).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get author by id", key = {"get-author"})
    public String getAuthorById(Long id) {
        Author author;
        try {
            author = authorService.getAuthorById(id);
        } catch (RecordNotFoundException e) {
            return shellHelper.getInfoMessage("Author not found");
        }

        return String.format("Author: %s", author);
    }

    @ShellMethod(value = "add author", key = {"add-author"})
    public String addAuthor() {
        String surname;
        String name;
        String patronymic;

        do {
            surname = inputReader.prompt("Surname");
        } while (!StringUtils.hasText(surname));

        do {
            name = inputReader.prompt("Name");
        } while (!StringUtils.hasText(name));

        patronymic = inputReader.prompt("Patronymic");

        if (!StringUtils.hasText(patronymic)) {
            patronymic = null;
        }

        Author author = new Author(surname, name, patronymic);

        if (authorService.addAuthor(author)) {
            return shellHelper.getSuccessMessage("Author added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding author");
        }
    }

    @ShellMethod(value = "delete author", key = {"delete-author"})
    public String deleteAuthor(long id) {
        if (authorService.deleteAuthorById(id)) {
            return shellHelper.getSuccessMessage("Author deleted successful");
        } else {
            return shellHelper.getErrorMessage("Error deleting author");
        }
    }

    @ShellMethod(value = "update author", key = {"update-author"})
    public String updateAuthor() {
        long id = -1;
        String surname;
        String name;
        String patronymic;

        do {
            try {
                id = Long.parseLong(inputReader.prompt("Id of the author being updated"));
            } catch (NumberFormatException e) {
                shellHelper.printError("Invalid id value: %s");
                id = -1;
            }
        } while (id < 1);

        do {
            surname = inputReader.prompt("Surname");
        } while (!StringUtils.hasText(surname));

        do {
            name = inputReader.prompt("Name");
        } while (!StringUtils.hasText(name));

        patronymic = inputReader.prompt("Patronymic");

        if (!StringUtils.hasText(patronymic)) {
            patronymic = null;
        }

        Author author = new Author(id, surname, name, patronymic);

        if (authorService.updateAuthor(author)) {
            return shellHelper.getSuccessMessage("Author updated successful");
        } else {
            return shellHelper.getErrorMessage("Error updating author");
        }
    }
}
