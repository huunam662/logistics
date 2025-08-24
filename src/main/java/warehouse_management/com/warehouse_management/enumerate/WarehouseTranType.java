package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;


import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum WarehouseTranType {
    // Điều chuyển nội bộ (kho VN → kho VN)
    DEST_TO_DEST_TRANSFER(
            "DEST_TO_DEST_TRANSFER",
            "DCNB"
    ),

    // Xuất từ kho đi (TQ) → kho đến (VN)
    DEPARTURE_TO_DEST_TRANSFER(
            "DEPARTURE_TO_DEST_TRANSFER",
            "TQ sang VN"
    ),

    // Nhập sản phẩm vào kho có nhiều cách nhập (excel, form)
    DATA_ENTRY(
            "DATA_ENTRY",
            "Nhập liệu"
    ),

    // Chuyển quyền bán hàng
    SALE_RIGHT_TRANSFER(
            "SALE_RIGHT_TRANSFER",
            "Chuyển quyền bán hàng"
    );

    private final String id;
    private final String title;

    WarehouseTranType(final String id, final String title) {
        this.id = id;
        this.title = title;
    }

    @Nullable
    public static WarehouseTranType fromId(String id) {
        for (WarehouseTranType at : WarehouseTranType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
