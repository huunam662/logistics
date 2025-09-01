package warehouse_management.com.warehouse_management.service.report;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface GenerateReportStrategy {
    /**
     * Lấy loại báo cáo mà strategy này xử lý (ví dụ: "PNK", "PXK").
     * Dùng cho Factory để đăng ký và tìm kiếm.
     * @return String loại báo cáo.
     */
    String getReportType();

    /**
     * Lấy dữ liệu cần thiết và chuẩn bị context cho template JXLS.
     * @param ticketId ID của phiếu giao dịch.
     * @return Map chứa dữ liệu cho context.
     */
    Map<String, Object> prepareContext(String ticketId);

    /**
     * Lấy tên file template cho loại báo cáo này.
     * @return Tên file, ví dụ: "PNK.xlsx".
     */
    String getTemplateFileName();

    /**
     * Thực hiện các thao tác tiền xử lý trên workbook trước khi JXLS chạy.
     * Ví dụ: dịch chuyển các dòng (shift rows).
     * @param workbook Workbook đã được tải từ template.
     * @param context Dữ liệu context đã được chuẩn bị.
     */
    void preprocessWorkbook(Workbook workbook, Map<String, Object> context);
}
