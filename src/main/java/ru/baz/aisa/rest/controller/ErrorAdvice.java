package ru.baz.aisa.rest.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class ErrorAdvice implements ProblemHandling {

}
