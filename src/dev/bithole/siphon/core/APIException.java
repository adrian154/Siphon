package dev.bithole.siphon.core;

// To avoid error message disclosure, all API errors are thrown with this special type
public class APIException extends Exception {

    public final int status;
    public APIException(int status, String message) {
        super(message);
        this.status = status;
    }

    // GSON can't serialize Throwables, so when an APIException is encountered, we build a response using this class
    public static class ErrorResponse {
        public final String message;
        public ErrorResponse(APIException ex) {
            this.message = ex.getMessage();
        }
    }

}
