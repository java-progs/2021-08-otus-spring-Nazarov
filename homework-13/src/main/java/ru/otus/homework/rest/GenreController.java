package ru.otus.homework.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.service.GenreService;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public String getAll(Model model) {
        val genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);
        return "genresList";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/addGenre")
    public String addGenre() {
        return "addGenre";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/addGenre")
    public String addGenre(Genre genre) {
        genreService.saveGenre(genre);
        return "redirect:/genres";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editGenre")
    public String editGenre(@RequestParam("id") long id, Model model) {
        fillGenreModel(id, model);
        return "editGenre";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editGenre")
    public String editGenre(Genre genre) {
        genreService.saveGenre(genre);
        return "redirect:/genres";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deleteGenre")
    public String confirmDeleteGenre(@RequestParam("id") long id, Model model) {
        fillGenreModel(id, model);
        return "deleteGenre";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteGenre")
    public String deleteGenre(@RequestParam("id") long id, Model model) {
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
