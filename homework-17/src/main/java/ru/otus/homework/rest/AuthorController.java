package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.service.AuthorService;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService service;

    @GetMapping("/authors")
    public String getAll(Model model) {
        val authorsList = service.getAllAuthors();
        model.addAttribute("authors", authorsList);
        return "authorsList";
    }

    @GetMapping("/addAuthor")
    public String addAuthor() {
        return "addAuthor";
    }

    @PostMapping("/addAuthor")
    public String addAuthor(Author author) {
        service.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/editAuthor")
    public String editAuthor(@RequestParam("id") String id, Model model) {
        fillAuthor(id, model);
        return "editAuthor";
    }

    @PostMapping("/editAuthor")
    public String editAuthor(Author author) {
        service.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/deleteAuthor")
    public String confirmDeleteAuthor(@RequestParam("id") String id, Model model) {
        fillAuthor(id, model);
        return "deleteAuthor";
    }

    @PostMapping("/deleteAuthor")
    public String deleteAuthor(@RequestParam("id") String id, Model model) {
        try {
            service.deleteAuthorById(id);
        } catch (ViolationOfConstraintException e) {
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

    private void fillAuthor(String id, Model model) {
        try {
            val author = service.getAuthorById(id);
            model.addAttribute("author", author);
        } catch (Exception e) {
            model.addAttribute("error", "Author not found");
        }
    }

}
