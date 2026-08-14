package com.sportstore.application.port.out;

/**
 * Exception applicative du port de persistance.
 * <p>
 * Les adaptateurs secondaires y mappent leurs exceptions techniques (SQLException,
 * DataAccessException, ...) : aucune exception de framework ne remonte au-dessus de l'adaptateur.
 */
public class ArticleStorageException extends RuntimeException {

    public ArticleStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
