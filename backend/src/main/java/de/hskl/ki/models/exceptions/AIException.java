package de.hskl.ki.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class AIException extends RuntimeException {
    private Class<?> relatedClass;

    public AIException() {
        super();
    }

    public AIException(String message, Throwable cause) {
        super(message, cause);
    }

    public AIException(String message, Class<?> relatedClass) {
        super(message);
        this.relatedClass = relatedClass;
    }

    public AIException(Throwable cause) {
        super(cause);
    }

    public Class<?> getRelatedClass() {
        return relatedClass;
    }
}
