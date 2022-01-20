package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.BookService;
import ru.otus.homework.service.GenreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final String DELIMITER = ";";

    @GetMapping(value = {"/", "/books"})
    public String getAll(Model model) {
        val bookList = bookService.getAllBooks();
        model.addAttribute("source", "Books");
        model.addAttribute("books", bookList);
        return "bookList";
    }

    @GetMapping(value = "/books", params = "author")
    public String getByAuthor(@RequestParam("author") String authorId, Model model) {
        Author author;
        try {
            author = authorService.getAuthorById(authorId);
        } catch (Exception e) {
            model.addAttribute("error", "Author and books not found");
            return "bookList";
        }

        val bookList = bookService.getByAuthor(authorId);
        var authorName = String.format("%s %s", author.getSurname(), author.getName());
        if (!(author.getPatronymic() == null) && author.getPatronymic().length() > 0 ) {
            authorName += " " + author.getPatronymic();
        }
        model.addAttribute("source", "Books by " + authorName);
        model.addAttribute("books", bookList);
        return "bookList";
    }

    @GetMapping(value = "/books", params = "genre")
    public String getByGenre(@RequestParam("genre") String genreId, Model model) {
        Genre genre;
        try {
            genre = genreService.getGenreById(genreId);
        } catch (Exception e) {
            model.addAttribute("error", "Genre and books not found");
            return "bookList";
        }

        val bookList = bookService.getByGenre(genreId);
        model.addAttribute("source", genre.getName() + " books");
        model.addAttribute("books", bookList);
        return "bookList";
    }

    @GetMapping(value = "/newBook")
    public String newBookForm(Model model) {
        val authors = authorService.getAllAuthors();
        val genres = genreService.getAllGenres();
        model.addAttribute("draftBook", new Book());
        model.addAttribute("allowedAuthors", authors);
        model.addAttribute("allowedGenres", genres);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"bookId", "name", "isbn", "addAuthor", "newAuthorId", "authorsId", "genresId"})
    public String bookAddAuthor(String bookId, String name, String isbn, String newAuthorId,
                                   String authorsId, String genresId, Model model) {
        val authorsIdList = splitString(authorsId, DELIMITER);
        authorsIdList.add(newAuthorId);
        val bookAuthors = authorService.getAllById(authorsIdList);

        val genresIdList = splitString(genresId, DELIMITER);
        val bookGenres = genreService.getAllById(genresIdList);

        fillDraftBookModel(bookId, name, isbn, bookAuthors, bookGenres, model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"bookId", "name", "isbn", "addGenre", "newGenreId", "authorsId", "genresId"})
    public String bookAddGenre(String bookId, String name, String isbn, String newGenreId,
                                  String authorsId, String genresId, Model model) {
        val genresIdList = splitString(genresId, DELIMITER);
        genresIdList.add(newGenreId);
        val bookGenres = genreService.getAllById(genresIdList);

        val authorsIdList = splitString(authorsId, DELIMITER);
        val bookAuthors = authorService.getAllById(authorsIdList);

        fillDraftBookModel(bookId, name, isbn, bookAuthors, bookGenres, model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"bookId", "name", "isbn", "deletedAuthorId", "authorsId", "genresId"})
    public String bookDeleteAuthor(String bookId, String name, String isbn, String deletedAuthorId,
                                   String authorsId, String genresId, Model model) {
        val authorsIdList = splitString(authorsId, DELIMITER);
        authorsIdList.remove(deletedAuthorId);
        val bookAuthors = authorService.getAllById(authorsIdList);

        val genresIdList = splitString(genresId, DELIMITER);
        val bookGenres = genreService.getAllById(genresIdList);

        fillDraftBookModel(bookId, name, isbn, bookAuthors, bookGenres, model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"bookId", "name", "isbn", "deletedGenreId", "authorsId", "genresId"})
    public String bookDeleteGenre(String bookId, String name, String isbn, String deletedGenreId,
                                  String authorsId, String genresId, Model model) {
        val genresIdList = splitString(genresId, DELIMITER);
        genresIdList.remove(deletedGenreId);
        val bookGenres = genreService.getAllById(genresIdList);

        val authorsIdList = splitString(authorsId, DELIMITER);
        val bookAuthors = authorService.getAllById(authorsIdList);

        fillDraftBookModel(bookId, name, isbn, bookAuthors, bookGenres, model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"bookId", "name", "isbn", "authorsId", "genresId"})
    public String bookSave(String bookId, String name, String isbn, String authorsId, String genresId, Model model) {
        val genresIdList = splitString(genresId, DELIMITER);
        val bookGenres = genreService.getAllById(genresIdList);

        val authorsIdList = splitString(authorsId, DELIMITER);
        val bookAuthors = authorService.getAllById(authorsIdList);

        val book = new Book(name, isbn, bookAuthors, bookGenres, null);
        if (bookId != null)  {
            book.setId(bookId);
        }

        bookService.saveBook(book);

        return "redirect:/books";
    }

    @GetMapping(value = "/editBook", params = "id")
    public String updateBook(String id, Model model) {
        Book book;

        try {
            book = bookService.getBookById(id);
        } catch (RecordNotFoundException e) {
            model.addAttribute("error", "Book not found");
            return "book";
        }

        fillDraftBookModel(book.getId(), book.getName(), book.getIsbn(), book.getAuthorsList(), book.getGenresList(), model);

        return "book";
    }

    @GetMapping(value = "/deleteBook")
    public String confirmDeleteBook(@RequestParam("id") String id, Model model) {
        fillBookModel(id, model);
        return "deleteBook";
    }

    @PostMapping(value = "/deleteBook")
    public String deleteBook(@RequestParam("id") String id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }

    @GetMapping(value = "/bookDetails")
    public String showBook(@RequestParam("id") String id, Model model) {
        fillBookModel(id, model);
        return "bookDetails";
    }

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

    private void fillDraftBookModel(String bookId, String name, String isbn, List<Author> bookAuthors, List<Genre> bookGenres, Model model) {
        val allAuthors = authorService.getAllAuthors();
        val allGenres = genreService.getAllGenres();
        model.addAttribute("bookId", bookId);
        model.addAttribute("bookAuthorsId", bookAuthors.stream().map(a -> a.getId()).collect(Collectors.joining(DELIMITER)));
        model.addAttribute("bookGenresId", bookGenres.stream().map(a -> a.getId()).collect(Collectors.joining(DELIMITER)));
        model.addAttribute("allowedAuthors", excludeObjectsList(allAuthors, bookAuthors));
        model.addAttribute("allowedGenres", excludeObjectsList(allGenres, bookGenres));
        model.addAttribute("draftBook", new Book(name, isbn, bookAuthors, bookGenres, null));
    }

    private List<String> splitString(String string, String delimiter) {
        return Arrays.stream(string.split(delimiter)).filter(s -> StringUtils.isNotEmpty(s)).collect(Collectors.toList());
    }

    private <T> List<T> excludeObjectsList(List<T> list, List<T> excludeList) {
        return list.stream().filter(o -> !excludeList.contains(o)).collect(Collectors.toList());
    }
}
