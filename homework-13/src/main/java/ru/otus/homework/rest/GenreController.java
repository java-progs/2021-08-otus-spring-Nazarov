package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.service.GenreService;
import ru.otus.homework.service.SecureService;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final SecureService secureService;

    @GetMapping("/genres")
    public String getAll(Model model) {
        val genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);
        return "genresList";
    }

    @GetMapping("/addGenre")
    public String addGenre() {
        secureService.checkRoleAdmin();
        return "addGenre";
    }

    @PostMapping("/addGenre")
    public String addGenre(Genre genre) {
        secureService.checkRoleAdmin();
        genreService.saveGenre(genre);
        return "redirect:/genres";
    }

    @GetMapping("/editGenre")
    public String editGenre(@RequestParam("id") long id, Model model) {
        secureService.checkRoleAdmin();
        fillGenreModel(id, model);
        return "editGenre";
    }

    @PostMapping("/editGenre")
    public String editGenre(Genre genre) {
        secureService.checkRoleAdmin();
        genreService.saveGenre(genre);
        return "redirect:/genres";
    }
    
    @GetMapping("/deleteGenre")
    public String confirmDeleteGenre(@RequestParam("id") long id, Model model) {
        secureService.checkRoleAdmin();
        fillGenreModel(id, model);
        return "deleteGenre";
    }

    @PostMapping("/deleteGenre")
    public String deleteGenre(@RequestParam("id") long id, Model model) {
        secureService.checkRoleAdmin();
        try {
            genreService.deleteGenreById(id);
        } catch (DataIntegrityViolationException e) {
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
    
    private void fillGenreModel(long genreId, Model model) {
        try {
            val genre = genreService.getGenreById(genreId);
            model.addAttribute("genre", genre);
        } catch (Exception e) {
            model.addAttribute("error", "Genre not found");
        }
    }
}
