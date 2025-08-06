package warehouse_management.com.warehouse_management.exceptions.errormsg;

public interface BaseErrorCode {
    String getCode();          // Mã lỗi (ví dụ: "ERR_001")
    String getKey();       // Mô tả lỗi
    int getHttpStatus();
}
