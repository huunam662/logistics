package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum ContainerStatus implements EnumClass<String> {

    REJECTED("REJECTED"),            // Hủy
    PENDING("PENDING"),          // Chờ xác nhận
    APPROVED("APPROVED"),        // Đã xác nhận
    HAD_DATE("HAD_DATE"),        // Đã có ngày
    IN_TRANSIT("IN_TRANSIT"),    // Đang đi đường
    UN_INSPECTED("UN_INSPECTED"),// Chưa đăng kiểm
    COMPLETED("COMPLETED");      // Hoàn tất

    private final String id;

    ContainerStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ContainerStatus fromId(String id) {
        for (ContainerStatus status : ContainerStatus.values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }
}