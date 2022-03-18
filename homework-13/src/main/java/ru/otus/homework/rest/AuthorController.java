package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Author;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.SecureService;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final SecureService secureService;

    @GetMapping("/authors")
    public String getAll(Model model) {
        val authorsList = authorService.getAllAuthors();
        model.addAttribute("authors", authorsList);
        return "authorsList";
    }

    @GetMapping("/addAuthor")
    public String addAuthor() {
        secureService.checkRoleAdmin();
        return "addAuthor";
    }

    @PostMapping("/addAuthor")
    public String addAuthor(Author author) {
        secureService.checkRoleAdmin();
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/editAuthor")
    public String editAuthor(@RequestParam("id") long id, Model model) {
        secureService.checkRoleAdmin();
        fillAuthor(id, model);
        return "editAuthor";
    }

    @PostMapping("/editAuthor")
    public String editAuthor(Author author) {
        secureService.checkRoleAdmin();
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/deleteAuthor")
    public String confirmDeleteAuthor(@RequestParam("id") long id, Model model) {
        secureService.checkRoleAdmin();
        fillAuthor(id, model);
        return "deleteAuthor";
    }

    @PostMapping("/deleteAuthor")
    public String deleteAuthor(@RequestParam("id") long id, Model model) {
        secureService.checkRoleAdmin();
        try {
            authorService.deleteAuthorById(id);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Delete the authors books before deleting author");
            model.addAttribute("authorId", id);
            return "deleteAuthor";
        } catch (Exception e) {
            model.addAttribute("error", "Error");
            model.addAttribute("authorId", id);
            return "deleteAuthor";
        }

        return "redirect:/authors";
    }

    private void fillAuthor(long id, Model model) {
        try {
            val author = authorService.getAuthorById(id);
            model.addAttribute("author", author);
        } catch (Exception e) {
            model.addAttribute("error", "Author not found");
        }
    }

}
