package vcms.enums;

public enum ScreeningResult {
    ELIGIBLE("Đủ điều kiện"),
    NOT_ELIGIBLE("Không đủ điều kiện");

    private final String label;

    ScreeningResult(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
