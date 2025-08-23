package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum WarehouseSubTranType {
    // ===== Nhập Excel vào kho chờ sản xuất (VN) =====
    EXCEL_TO_PRODUCTION_PRODUCT(
            "EXCEL_TO_PRODUCTION_PRODUCT",
            "Nhập excel xe/phụ kiện"
    ),
    EXCEL_TO_PRODUCTION_SPARE_PART(
            "EXCEL_TO_PRODUCTION_SPARE_PART",
            "Nhập excel phụ tùng"
    ),

    // ===== Nhập Form UI vào kho chờ sản xuất (VN) =====
    FORM_TO_PRODUCTION_PRODUCT(
            "FORM_TO_PRODUCTION_PRODUCT",
            "Nhập form xe/phụ kiện"
    ),
    FORM_TO_PRODUCTION_SPARE_PART(
            "FORM_TO_PRODUCTION_SPARE_PART",
            "Nhập form phụ tùng"
    ),

    // ===== Nhập Form UI vào kho đến (VN) =====
    FORM_TO_DEST_PRODUCT(
            "FORM_TO_DEST_PRODUCT",
            "Nhập form xe/phụ kiện"
    ),
    FORM_TO_DEST_SPARE_PART(
            "FORM_TO_DEST_SPARE_PART",
            "Nhập form phụ tùng"
    );
    private final String id;
    private final String title;

    WarehouseSubTranType(final String id, final String title) {
        this.id = id;
        this.title = title;
    }

    @Nullable
    public static WarehouseSubTranType fromId(String id) {
        for (WarehouseSubTranType st : WarehouseSubTranType.values()) {
            if (st.getId().equals(id)) {
                return st;
            }
        }
        return null;
    }
}