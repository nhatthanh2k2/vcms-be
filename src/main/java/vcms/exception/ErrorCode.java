package vcms.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    INVALID_KEY(1001, "Uncategorized error"),
    USER_EXISTED(1002, "User existed"),
    USERNAME_INVALID(1003, "Username invalid"),
    INVALID_PASSWORD(1004, "Password invalid"),
    EMPLOYEE_NOT_EXISTED(1005, "Employee not existed"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
