package com.miro.api.widgets.testtask.exceptions;

import java.util.List;

/**
 * Class that is represents api error response.
 */
public class ErrorResponse {
    /**
     * Main error message.
     */
    private final String message;

    /**
     * Additional error information.
     */
    private final List<String> details;

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }
}