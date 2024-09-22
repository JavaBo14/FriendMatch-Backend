package com.bopao.common;

public enum TagStatus {
    WAIT("wait"),
    SUCCEED("succeed"),
    FAILED("failed");

    private final String status;

    TagStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }

    // 通过字符串获取枚举值的方法
    public static TagStatus fromString(String status) {
        for (TagStatus tagStatus : TagStatus.values()) {
            if (tagStatus.status.equalsIgnoreCase(status)) {
                return tagStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
