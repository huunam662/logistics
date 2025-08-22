package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum WarehouseSubTranType {
    // ===== Nhập Excel vào kho chờ sản xuất (VN) =====
    EXCEL_TO_PRODUCTION_PRODUCT(
            "EXCEL_TO_PRODUCTION_PRODUCT",
            "Import Excel: Xe/Phụ kiện vào kho chờ sản xuất"
    ),
    EXCEL_TO_PRODUCTION_SPARE_PART(
            "EXCEL_TO_PRODUCTION_SPARE_PART",
            "Import Excel: Phụ tùng vào kho chờ sản xuất"
    ),

    // ===== Nhập Form UI vào kho chờ sản xuất (VN) =====
    FORM_TO_PRODUCTION_PRODUCT(
            "FORM_TO_PRODUCTION_PRODUCT",
            "Nhập Form: Xe/Phụ kiện vào kho chờ sản xuất"
    ),
    FORM_TO_PRODUCTION_SPARE_PART(
            "FORM_TO_PRODUCTION_SPARE_PART",
            "Nhập Form: Phụ tùng vào kho chờ sản xuất"
    ),

    // ===== Nhập Form UI vào kho đến (VN) =====
    FORM_TO_DEST_PRODUCT(
            "FORM_TO_DEST_PRODUCT",
            "Nhập Form: Xe/Phụ kiện vào kho đến"
    ),
    FORM_TO_DEST_SPARE_PART(
            "FORM_TO_DEST_SPARE_PART",
            "Nhập Form: Phụ tùng vào kho đến"
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