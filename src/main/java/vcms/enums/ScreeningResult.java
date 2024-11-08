package vcms.enums;

import lombok.Getter;

@Getter
public enum ScreeningResult {
    ELIGIBLE("Đủ điều kiện"),
    NOT_ELIGIBLE("Không đủ điều kiện");

    private final String label;

    ScreeningResult(String label) {
        this.label = label;
    }

}
