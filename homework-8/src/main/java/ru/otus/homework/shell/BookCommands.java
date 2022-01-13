package ru.otus.homework.shell;

import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.BookService;
import ru.otus.homework.service.GenreService;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

import java.util.*;
import java.util.stream.Collectors;

@ShellComponent
public class BookCommands {

    private final String ID_DELIMITER = ",";

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public BookCommands(ShellHelper shellHelper, InputReader inputReader, BookService bookService, AuthorService authorService, GenreService genreService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @ShellMethod(value = "show books list", key = {"list-books"})
    public String getBooksList() {
        val booksList = bookService.getAllBooks();
        val authorsList = authorService.getAllAuthors();
        val genresList = genreService.getAllGenres();

        return String.format("Books list:%n%s",
                booksList.stream().map(b -> {
                    val bookAuthors = authorsList.stream().filter(a -> b.getAuthorsList().contains(a)).collect(Collectors.toList());
                    val bookGenres = genresList.stream().filter(g -> b.getGenresList().contains(g)).collect(Collectors.toList());
                    return shellHelper.getBookDescription(b, bookAuthors, bookGenres);
                }).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get book by id", key = {"get-book"})
    public String getBookById(String id) {
        Book book;
        List<Author> authorsList;
        List<Genre> genresList;

        try {
            book = bookService.getBookById(id);
            authorsList = book.getAuthorsList();
            genresList = book.getGenresList();
        } catch (RecordNotFoundException e) {
            return shellHelper.getInfoMessage("Book not found");
        }

        return String.format("Book: %s", shellHelper.getBookDescription(book, authorsList, genresList));
    }

    @ShellMethod(value = "add book", key = {"add-book"})
    public String addBook() {
        String name;
        String isbn;
        List<String> authorsId;
        List<String> genresId;

        do {
            name = inputReader.prompt("Book name");
        } while (!StringUtils.hasText(name));

        isbn = inputReader.prompt("ISBN");
        if (!StringUtils.hasText(isbn)) {
            isbn = null;
        }

        do {
            String authors = inputReader.prompt("Authors list");
            authorsId = parseMultipleIdList(authors);
            if (authorsId == null) {
                shellHelper.printError(String.format("Wrong authors list format: %s. Use delimiter: %s", authors, ID_DELIMITER));
            }
        } while (authorsId == null);

        do {
            String genres = inputReader.prompt("Genres list");
            genresId = parseMultipleIdList(genres);
            if (genresId == null) {
                shellHelper.printError(String.format("Wrong genres list format: %s. Use delimiter: %s", genres, ID_DELIMITER));
            }
        } while (genresId == null);

        if (bookService.saveBook(name, isbn, authorsId, genresId) != null) {
            return shellHelper.getSuccessMessage("Book added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding book");
        }
    }


    @ShellMethod(value = "delete book", key = {"delete-book"})
    public String deleteBook(String id) {
        try {
            bookService.deleteBookById(id);
            return shellHelper.getSuccessMessage("Book deleted successful");
        } catch (Exception e) {
            return shellHelper.getErrorMessage("Error deleting book");
        }
    }

    @ShellMethod(value = "update book", key = {"update-book"})
    public String updateBook() {
        String id;
        String name;
        String isbn;
        List<String> authorsId;
        List<String> genresId;

        do {
            id = inputReader.prompt("Id of the book being updated");
        } while (!StringUtils.hasText(id));

        do {
            name = inputReader.prompt("Book name");
        } while (!StringUtils.hasText(name));

        isbn = inputReader.prompt("ISBN");
        if (!StringUtils.hasText(isbn)) {
            isbn = null;
        }

        do {
            String authors = inputReader.prompt("Authors list");
            authorsId = parseMultipleIdList(authors);
            if (authorsId == null) {
                shellHelper.printError(String.format("Wrong authors list format: %s. Use delimiter: %s", authors, ID_DELIMITER));
            }
        } while (authorsId == null);

        do {
            String genres = inputReader.prompt("Genres list");
            genresId = parseMultipleIdList(genres);
            if (genresId == null) {
                shellHelper.printError(String.format("Wrong genres list format: %s. Use delimiter: %s", genres, ID_DELIMITER));
            }
        } while (genresId == null);

        if (bookService.updateBook(id, name, isbn, authorsId, genresId)) {
            return shellHelper.getSuccessMessage("Book updated successful");
        } else {
            return shellHelper.getErrorMessage("Error updating book");
        }
    }

    private List<String> parseMultipleIdList(String input) {
        if (input == null) {
            return null;
        }

        if (!StringUtils.hasText(input)) {
            return null;
        }

        String[] idArray = input.replace(" ", "").split(String.format("[%s]", ID_DELIMITER));

        return Arrays.stream(idArray).distinct().collect(Collectors.toList());
    }

}
