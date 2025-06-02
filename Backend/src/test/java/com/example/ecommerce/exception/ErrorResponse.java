package com.example.ecommerce.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String path;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private int status;

    public ErrorResponse(String path, String error, String message, LocalDateTime timestamp, int status) {
        this.path = path;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters et setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
