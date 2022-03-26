package ru.otus.homework.rest;

import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView errorHandler() throws Exception {
        val modelAndView = new ModelAndView();
        modelAndView.addObject("errorDescription", "Access denied");
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
