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
public class AuthorCommands {

    private final AuthorService authorService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public AuthorCommands(ShellHelper shellHelper, InputReader inputReader, AuthorService authorService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.authorService = authorService;
    }

    @ShellMethod(value = "show authors list", key = {"list-authors"})
    public String getAuthorsList() {
        val authorsList = authorService.getAllAuthors();
        return String.format("Authors list:%n%s",
                authorsList.stream().map(a -> getAuthorDescription(a)).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get author by id", key = {"get-author"})
    public String getAuthorById(Long id) {
        Author author;
        try {
            author = authorService.getAuthorById(id);
        } catch (Exception e) {
            return shellHelper.getInfoMessage("Author not found");
        }

        return String.format("Author: %s", getAuthorDescription(author));
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

        Author author = new Author(0, surname, name, patronymic);

        if (authorService.saveAuthor(author)) {
            return shellHelper.getSuccessMessage("Author added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding author");
        }
    }

    @ShellMethod(value = "delete author", key = {"delete-author"})
    public String deleteAuthor(long id) {
        try {
            authorService.deleteAuthorById(id);
            return shellHelper.getSuccessMessage("Author deleted successful");
        } catch (Exception e) {
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
                shellHelper.printError("Invalid id value");
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

    public String getAuthorDescription(Author author) {
        var description = new StringBuilder();

        description.append(String.format("   Id: %s, Surname: %s, Name: %s", author.getId(), author.getSurname(), author.getName()));

        if (author.getPatronymic() != null) {
            description.append(String.format(", Patronymic: %s", author.getPatronymic()));
        }

        description.append(String.format("%n"));

        return description.toString();
    }
}
