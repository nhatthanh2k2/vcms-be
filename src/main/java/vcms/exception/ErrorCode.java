package vcms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error",
                            HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.CONFLICT),
    USERNAME_INVALID(1003, "Username must be at least 3 characters",
                     HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters",
                     HttpStatus.BAD_REQUEST),
    NOT_EXISTED(1005, "Not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    UNDONE(1008, "Service Failed", HttpStatus.BAD_REQUEST),
    PASSWORDS_NOT_MATCH(1009, "Passwords do not match", HttpStatus.BAD_REQUEST),
    CREATE_FAILED(1010, "Create Failed", HttpStatus.BAD_REQUEST),
    UPDATE_FAILED(1011, "Update Failed", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
