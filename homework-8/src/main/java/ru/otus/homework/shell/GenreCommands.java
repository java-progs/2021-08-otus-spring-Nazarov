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
                genresList.stream().map(g -> shellHelper.getGenreDescription(g)).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get genre by id", key = {"get-genre"})
    public String getGenreById(String id) {
        Genre genre;
        try {
            genre = genreService.getGenreById(id);
        } catch (RecordNotFoundException e) {
            return shellHelper.getInfoMessage("Genre not found");
        }

        return String.format("Genre: %s", shellHelper.getGenreDescription(genre));
    }

    @ShellMethod(value = "add genre", key = {"add-genre"})
    public String addGenre() {
        String name;

        do {
            name = inputReader.prompt("Name");
        } while (!StringUtils.hasText(name));

        Genre genre = new Genre(name);

        if (genreService.saveGenre(genre) != null) {
            return shellHelper.getSuccessMessage("Genre added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding genre");
        }
    }

    @ShellMethod(value = "delete genre", key = {"delete-genre"})
    public String deleteGenre(String id) {
        try {
            genreService.deleteGenreById(id);
            return shellHelper.getSuccessMessage("Genre deleted successful");
        } catch (Exception e) {
            return shellHelper.getErrorMessage("Error deleting genre");
        }
    }

    @ShellMethod(value = "update genre", key = {"update-genre"})
    public String updateGenre() {
        String id;
        String name;

        do {
            id = inputReader.prompt("Id of the genre being updated");
        } while (!StringUtils.hasText(id));

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

}
