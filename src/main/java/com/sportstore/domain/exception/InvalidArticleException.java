package com.sportstore.domain.exception;

/**
 * Exception metier levee lorsqu'un invariant du domaine n'est pas respecte.
 */
public class InvalidArticleException extends RuntimeException {

    public InvalidArticleException(String message) {
        super(message);
    }
}
