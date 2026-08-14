package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.out.ArticleStorageException;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.exception.InvalidArticleException;
import com.sportstore.infrastructure.adapter.in.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Traduction des exceptions metier en reponses HTTP. Concerne uniquement l'adaptateur primaire.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleArticleNotFound(ArticleNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidArticleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArticle(InvalidArticleException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message.isBlank() ? "Invalid request payload" : message));
    }

    @ExceptionHandler(ArticleStorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageFailure(ArticleStorageException exception) {
        log.error("Echec de l'acces au stockage du catalogue", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Storage failure: " + exception.getMessage()));
    }
}
