package de.hskl.ki.resource;

import de.hskl.ki.models.ErrorDTO;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionResource {
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String processRuntimeException(RuntimeException e) {
        return "There was an error with your authentication details.";
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String processJWTSignatureException(RuntimeException e) {
        return "Your token has expired.";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO processRuntimeException(Exception e) {
        e.printStackTrace();
        return new ErrorDTO(e.getMessage());
    }

}

