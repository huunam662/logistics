package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum RepairStatus {

    PENDING("PENDING", "Chờ xác nhận"),
    REPAIRING("REPAIRING", "Đang sửa chữa"),
    COMPLETED("COMPLETED", "Hoàn tất");

    private final String id;
    private final String value;

    RepairStatus(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Nullable
    public static RepairStatus fromId(String id) {
        for (RepairStatus at : RepairStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
