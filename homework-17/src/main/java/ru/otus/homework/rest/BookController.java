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
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.BookService;
import ru.otus.homework.service.GenreService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.homework.dto.Mapper.DELIMITER;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final Mapper mapper;

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
        model.addAttribute("source", "Books by " + author.getFullName());
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

    @PostMapping(value = "/newBook", params = {"addAuthor", "newAuthorId"})
    public String bookAddAuthor(BookDto bookDto, String newAuthorId, Model model) {
        val authorsId = bookDto.getAuthorsId() + DELIMITER + newAuthorId;
        bookDto.setAuthorsId(authorsId);

        fillDraftBookModel(mapper.toBook(bookDto), model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"addGenre", "newGenreId"})
    public String bookAddGenre(BookDto bookDto, String newGenreId, Model model) {
        val genresId = bookDto.getGenresId() + DELIMITER + newGenreId;
        bookDto.setGenresId(genresId);

        fillDraftBookModel(mapper.toBook(bookDto), model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"deletedAuthorId"})
    public String bookDeleteAuthor(BookDto bookDto, String deletedAuthorId, Model model) {
        val authorsIdList = splitString(bookDto.getAuthorsId(), DELIMITER);
        authorsIdList.remove(deletedAuthorId);

        bookDto.setAuthorsId(authorsIdList.stream().collect(Collectors.joining(DELIMITER)));

        fillDraftBookModel(mapper.toBook(bookDto), model);
        return "book";
    }

    @PostMapping(value = "/newBook", params = {"deletedGenreId"})
    public String bookDeleteGenre(BookDto bookDto, String deletedGenreId, Model model) {
        val genresIdList = splitString(bookDto.getGenresId(), DELIMITER);
        genresIdList.remove(deletedGenreId);

        bookDto.setGenresId(genresIdList.stream().collect(Collectors.joining(DELIMITER)));

        fillDraftBookModel(mapper.toBook(bookDto), model);
        return "book";
    }

    @PostMapping(value = "/newBook")
    public String bookSave(BookDto bookDto, Model model) {
        val book = mapper.toBook(bookDto);
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

        fillDraftBookModel(book, model);

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

    private void fillBookModel(String bookId, Model model) {
        try {
            val book = bookService.getBookById(bookId);
            model.addAttribute("book", book);
        } catch (Exception e) {
            model.addAttribute("error", "Book not found");
        }
    }

    private void fillDraftBookModel(Book book, Model model) {
        val allAuthors = authorService.getAllAuthors();
        val allGenres = genreService.getAllGenres();

        val authorsId = book.getAuthorsList().stream().map(a -> a.getId()).collect(Collectors.joining(DELIMITER));
        val genresId = book.getGenresList().stream().map(g -> g.getId()).collect(Collectors.joining(DELIMITER));

        val allowedAuthors = excludeObjectsList(allAuthors, book.getAuthorsList());
        val allowedGenres = excludeObjectsList(allGenres, book.getGenresList());

        model.addAttribute("bookId", book.getId());
        model.addAttribute("bookAuthorsId", authorsId);
        model.addAttribute("bookGenresId", genresId);
        model.addAttribute("allowedAuthors", allowedAuthors);
        model.addAttribute("allowedGenres", allowedGenres);
        model.addAttribute("draftBook", book);
    }

    private List<String> splitString(String string, String delimiter) {
        return Arrays.stream(string.split(delimiter)).filter(s -> StringUtils.isNotEmpty(s)).collect(Collectors.toList());
    }

    private <T> List<T> excludeObjectsList(List<T> list, List<T> excludeList) {
        return list.stream().filter(o -> !excludeList.contains(o)).collect(Collectors.toList());
    }

}
