package com.sportstore.domain.exception;

public class InvalidArticleException extends RuntimeException {

    public InvalidArticleException(String message) {
        super(message);
    }
}
