package de.hskl.ki.models;

import java.sql.Timestamp;
import java.util.Date;

public class ErrorDTO {
    private String message;
    private Timestamp time = new Timestamp(new Date().getTime());

    public ErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
