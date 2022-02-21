package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.service.GenreService;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @GetMapping("/genres")
    public String getAll(Model model) {
        val genres = service.getAllGenres();
        model.addAttribute("genres", genres);
        return "genresList";
    }

    @GetMapping("/addGenre")
    public String addGenre() {
        return "addGenre";
    }

    @PostMapping("/addGenre")
    public String addGenre(Genre genre) {
        service.saveGenre(genre);
        return "redirect:/genres";
    }

    @GetMapping("/editGenre")
    public String editGenre(@RequestParam("id") String id, Model model) {
        fillGenreModel(id, model);
        return "editGenre";
    }

    @PostMapping("/editGenre")
    public String editGenre(Genre genre) {
        service.saveGenre(genre);
        return "redirect:/genres";
    }
    
    @GetMapping("/deleteGenre")
    public String confirmDeleteGenre(@RequestParam("id") String id, Model model) {
        fillGenreModel(id, model);
        return "deleteGenre";
    }

    @PostMapping("/deleteGenre")
    public String deleteGenre(@RequestParam("id") String id, Model model) {
        try {
            service.deleteGenreById(id);
        } catch (ViolationOfConstraintException e) {
            model.addAttribute("error", "Delete the genre books before deleting genre");
            model.addAttribute("genreId", id);
            return "deleteGenre";
        } catch (Exception e) {
            model.addAttribute("error", "Error");
            model.addAttribute("genreId", id);
            return "deleteGenre";
        }

        return "redirect:/genres";
    }
    
    private void fillGenreModel(String genreId, Model model) {
        try {
            val genre = service.getGenreById(genreId);
            model.addAttribute("genre", genre);
        } catch (Exception e) {
            model.addAttribute("error", "Genre not found");
        }
    }
}
