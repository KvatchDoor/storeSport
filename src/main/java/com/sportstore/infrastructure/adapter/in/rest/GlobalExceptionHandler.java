package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.out.ArticleStorageException;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.exception.InvalidArticleException;
import com.sportstore.domain.exception.OutOfStockException;
import com.sportstore.infrastructure.adapter.in.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String INVALID_PAYLOAD_MESSAGE = "Invalid request payload";

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleArticleNotFound(ArticleNotFoundException exception) {
        log.debug("404 - article inconnu : {}", exception.articleName());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidArticleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArticle(InvalidArticleException exception) {
        log.debug("400 - invariant du domaine viole : {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException exception) {
        log.debug("400 - rupture de stock : {}", exception.articleName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.debug("400 - corps non conforme au contrat : {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message.isBlank() ? INVALID_PAYLOAD_MESSAGE : message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        log.debug("400 - corps de requete illisible : {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(INVALID_PAYLOAD_MESSAGE));
    }

    @ExceptionHandler(ArticleStorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageFailure(ArticleStorageException exception) {
        log.error("Echec de l'acces au stockage du catalogue", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Storage failure: " + exception.getMessage()));
    }
}
