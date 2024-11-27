package vcms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    CUSTOMER_EXISTED(1002, "Customer existed", HttpStatus.CONFLICT),
    CUSTOMER_NOT_EXISTED(1003, "Customer not existed", HttpStatus.NOT_FOUND),
    EMPLOYEE_NOT_EXISTED(1004, "Employee not existed", HttpStatus.NOT_FOUND),
    NOT_EXISTED(1005, "Not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    UNDONE(1008, "Service Failed", HttpStatus.BAD_REQUEST),
    PASSWORDS_NOT_MATCH(1009, "Passwords do not match", HttpStatus.BAD_REQUEST),
    CREATE_FAILED(1010, "Create Failed", HttpStatus.BAD_REQUEST),
    UPDATE_FAILED(1011, "Update Failed", HttpStatus.BAD_REQUEST),
    VACCINE_QUANTITY_INSUFFICIENT(1012, "Vaccine quantity is insufficient", HttpStatus.BAD_REQUEST),
    DELETE_FAILED(1013, "Cannot delete due to dependencies", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE(1014, "Invalid image", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(1015, "Account is locked", HttpStatus.FORBIDDEN);


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
