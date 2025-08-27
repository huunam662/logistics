package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DeliveryOrderStatus {

    // Cho thêm hàng
    UN_DELIVERED("UN_DELIVERED"),   // Chưa giao
    DELIVERING("DELIVERING"),   // Đang giao
    HOLD("HOLD"),    // Giữ hàng
    // Không cho thêm hàng
    COMPLETED("COMPLETED"), // Hoàn tất
    REJECTED("REJECTED");   // Đã hủy

    private final String value;

    DeliveryOrderStatus(final String value) {
        this.value = value;
    }

    public static DeliveryOrderStatus fromValue(final String value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

}
