package com.lickthespring.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class MyControllerAdvice {
    
    @ExceptionHandler({RuntimeException.class})
    public ModelAndView handle(RuntimeException ex, Model model) {
        System.out.println("MyControllerAdvice.handle() called");

        ModelAndView response = new ModelAndView("/WEB-INF/views/error.jsp");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("message", ex.getMessage());

        return response;
    }

}
