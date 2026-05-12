package ru.mentee.power.crm.contact.adapter.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentee.power.crm.contact.usecase.service.LinkedPersonNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setProperty("errorCode", "VALIDATION_FAILED");
    return problem;
  }

  @ExceptionHandler(IllegalStateException.class)
  public ProblemDetail handleIllegalState(IllegalStateException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setProperty("errorCode", "PERSON_EMAIL_CONFLICT");
    return problem;
  }

    @ExceptionHandler(PersonNotFoundException.class)
    public ProblemDetail handlePersonNotFound(PersonNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setProperty("errorCode", "PERSON_NOT_FOUND");
        return problem;
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ProblemDetail handleCompanyNotFound(CompanyNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setProperty("errorCode", "COMPANY_NOT_FOUND");
        return problem;
    }

    @ExceptionHandler(LinkedPersonNotFoundException.class)
    public ProblemDetail handleLinkedPersonNotFound(LinkedPersonNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setProperty("errorCode", "PERSON_NOT_FOUND");
        return problem;
    }
}