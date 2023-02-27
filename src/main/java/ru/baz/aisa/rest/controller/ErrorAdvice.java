package ru.baz.aisa.rest.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import ru.baz.aisa.exception.ApplicationException;

@RestControllerAdvice
public class ErrorAdvice implements ProblemHandling {

    @ExceptionHandler(ApplicationException.class)
    public Problem handleException(ApplicationException ex) {
        return Problem.builder().withDetail(ex.getMessage()).withStatus(Status.INTERNAL_SERVER_ERROR).build();
//        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
