package ru.otus.homework.shell;

import lombok.val;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;
import ru.otus.homework.domain.Book;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.BookService;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ShellComponent
public class BookCommands {

    private final String ID_DELIMITER = ",";

    private final BookService bookService;
    private final ShellHelper shellHelper;
    private final InputReader inputReader;

    public BookCommands(ShellHelper shellHelper, InputReader inputReader, BookService bookService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.bookService = bookService;
    }

    @ShellMethod(value = "show books list", key = {"list-books"})
    public String getBooksList() {
        val booksList = bookService.getAllBooks();
        return String.format("Books list:%n%s",
                booksList.stream().map(Book::toString).collect(Collectors.joining("\n")));
    }

    @ShellMethod(value = "get book by id", key = {"get-book"})
    public String getBookById(Long id) {
        Book book;
        try {
            book = bookService.getBookById(id);
        } catch (RecordNotFoundException e) {
            return shellHelper.getInfoMessage("Book not found");
        }

        return String.format("Book: %s", book);
    }

    @ShellMethod(value = "add book", key = {"add-book"})
    public String addBook() {
        String name;
        String isbn;
        Long[] authorsArray;
        Long[] genresArray;

        do {
            name = inputReader.prompt("Book name");
        } while (!StringUtils.hasText(name));

        isbn = inputReader.prompt("ISBN");
        if (!StringUtils.hasText(isbn)) {
            isbn = null;
        }

        do {
            String authors = inputReader.prompt("Authors list");
            authorsArray = parseMultipleIdList(authors);
            if (authorsArray == null) {
                shellHelper.printError(String.format("Wrong authors list format: %s. Use delimiter: %s", authors, ID_DELIMITER));
            }
        } while (authorsArray == null);

        do {
            String genres = inputReader.prompt("Genres list");
            genresArray = parseMultipleIdList(genres);
            if (genresArray == null) {
                shellHelper.printError(String.format("Wrong genres list format: %s. Use delimiter: %s", genres, ID_DELIMITER));
            }
        } while (genresArray == null);

        if (bookService.addBook(name, isbn, authorsArray, genresArray)) {
            return shellHelper.getSuccessMessage("Book added successful");
        } else {
            return shellHelper.getErrorMessage("Error adding book");
        }
    }


    @ShellMethod(value = "delete book", key = {"delete-book"})
    public String deleteBook(long id) {
        if (bookService.deleteBookById(id)) {
            return shellHelper.getSuccessMessage("Book deleted successful");
        } else {
            return shellHelper.getErrorMessage("Error deleting author");
        }
    }

    @ShellMethod(value = "update book", key = {"update-book"})
    public String updateAuthor() {
        long id = -1;
        String name;
        String isbn;
        Long[] authorsArray;
        Long[] genresArray;

        do {
            try {
                id = Long.parseLong(inputReader.prompt("Id of the book being updated"));
            } catch (NumberFormatException e) {
                shellHelper.printError("Invalid id value: %s");
                id = -1;
            }
        } while (id < 1);

        do {
            name = inputReader.prompt("Book name");
        } while (!StringUtils.hasText(name));

        isbn = inputReader.prompt("ISBN");
        if (!StringUtils.hasText(isbn)) {
            isbn = null;
        }

        do {
            String authors = inputReader.prompt("Authors list");
            authorsArray = parseMultipleIdList(authors);
            if (authorsArray == null) {
                shellHelper.printError(String.format("Wrong authors list format: %s. Use delimiter: %s", authors, ID_DELIMITER));
            }
        } while (authorsArray == null);

        do {
            String genres = inputReader.prompt("Genres list");
            genresArray = parseMultipleIdList(genres);
            if (genresArray == null) {
                shellHelper.printError(String.format("Wrong genres list format: %s. Use delimiter: %s", genres, ID_DELIMITER));
            }
        } while (genresArray == null);

        if (bookService.updateBook(id, name, isbn, authorsArray, genresArray)) {
            return shellHelper.getSuccessMessage("Book updated successful");
        } else {
            return shellHelper.getErrorMessage("Error updating author");
        }
    }

    private Long[] parseMultipleIdList(String input) {
        if (input == null) {
            return null;
        }

        if (!StringUtils.hasText(input)) {
            return null;
        }

        Set<Long> idList = new HashSet<>();

        String[] idArray = input.replace(" ", "").split(String.format("[%s]", ID_DELIMITER));

        for (String id : idArray) {
            try {
                idList.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return idList.toArray(new Long[idList.size()]);
    }
}
