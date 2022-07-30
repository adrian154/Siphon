package dev.bithole.siphon.core.api;

// To avoid error message disclosure, all API errors are thrown with this special type
public class APIException extends Exception {

    public final int status;

    public APIException(int status, String message) {
        super(message);
        this.status = status;
    }

    public APIException(int status) {
        this(status, getMessage(status));
    }

    private static String getMessage(int status) {
        return switch (status) {
            case 400 -> "Bad request";
            default -> "No further info available";
        };
    }

    // GSON can't serialize Throwables, so when an APIException is encountered, we build a response using this class
    public static class ErrorResponse {

        public final String error;

        public ErrorResponse(APIException ex) {
            this.error = ex.getMessage();
        }
        public ErrorResponse(String message) {
            this.error = message;
        }

    }

}
