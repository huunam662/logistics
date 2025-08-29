package warehouse_management.com.warehouse_management.dto.report_inventory.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class ReportParamsDto {

    @Schema(description = "Số trang.")
    private Integer page = 1;

    @Schema(description = "Kích thước trang.")
    private Integer size = 10;

    @Schema(description = "Lọc theo mã PO number.")
    private String poNumber;

    @Schema(description = "Lọc theo mã Model.")
    private String model;

    @Schema(description = "Lọc theo khách hàng (đại lý).")
    private String agent;

    @Schema(description = "Lọc theo từ ngày yêu cầu. Format (yyyy-MM-dd)")
    private String fromDate;

    @Schema(description = "Lọc theo đến ngày yêu cầu. Format (yyyy-MM-dd)")
    private String toDate;

    @Schema(description = "Lọc theo từ khóa trong tìm kiếm chung.")
    private String search;

    @Schema(description = "Kiểu báo cáo [[PRODUCTION (hang chờ sx), CONSIGNMENT (hàng ký gửi)]")
    private String typeReport;
}
