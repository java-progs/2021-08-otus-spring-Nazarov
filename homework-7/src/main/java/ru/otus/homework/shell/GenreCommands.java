package ru.otus.homework.shell;

import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.GenreService;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

import java.util.stream.Collectors;

@ShellComponent
public class GenreCommands {

    private final GenreService genreService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public GenreCommands(ShellHelper shellHelper, InputReader inputReader, GenreService genreService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.genreService = genreService;
    }

    @ShellMethod(value = "show genres list", key = {"list-genres"})
    public String getGenresList() {
        val genresList = genreService.getAllGenres();
        return String.format("Genres list:%n%s",
                genresList.stream().map(g -> getGenreDescription(g)).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get genre by id", key = {"get-genre"})
    public String getGenreById(Long id) {
        Genre genre;
        try {
            genre = genreService.getGenreById(id);
        } catch (RecordNotFoundException e) {
            return shellHelper.getInfoMessage("Genre not found");
        }

        return String.format("Genre: %s", getGenreDescription(genre));
    }

    @ShellMethod(value = "add genre", key = {"add-genre"})
    public String addGenre() {
        String name;

        do {
            name = inputReader.prompt("Name");
        } while (!StringUtils.hasText(name));

        Genre genre = new Genre(0, name);

        if (genreService.saveGenre(genre)) {
            return shellHelper.getSuccessMessage("Genre added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding genre");
        }
    }

    @ShellMethod(value = "delete genre", key = {"delete-genre"})
    public String deleteGenre(long id) {
        try {
            genreService.deleteGenreById(id);
            return shellHelper.getSuccessMessage("Genre deleted successful");
        } catch (Exception e) {
            return shellHelper.getErrorMessage("Error deleting genre");
        }
    }

    @ShellMethod(value = "update genre", key = {"update-genre"})
    public String updateGenre() {
        long id = -1;
        String name;

        do {
            try {
                id = Long.parseLong(inputReader.prompt("Id of the genre being updated"));
            } catch (NumberFormatException e) {
                shellHelper.printError("Invalid id value");
                id = -1;
            }
        } while (id < 1);

        do {
            name = inputReader.prompt("Name");
        } while (!StringUtils.hasText(name));

        Genre genre = new Genre(id, name);

        if (genreService.updateGenre(genre)) {
            return shellHelper.getSuccessMessage("Genre updated successful");
        } else {
            return shellHelper.getErrorMessage("Error updating genre");
        }
    }

    private String getGenreDescription(Genre genre) {
        val description = new StringBuilder();

        description.append(String.format("Id: %s, Name:%s%n", genre.getId(), genre.getName()));

        return description.toString();
    }
}
