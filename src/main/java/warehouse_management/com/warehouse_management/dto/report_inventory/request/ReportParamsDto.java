package warehouse_management.com.warehouse_management.dto.report_inventory.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springdoc.core.annotations.ParameterObject;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;

@EqualsAndHashCode(callSuper = true)
@Data
@ParameterObject
public class ReportParamsDto extends PageOptionsDto {

    @Parameter(description = "Kiểu báo cáo [PRODUCTION (hàng chờ sx), CONSIGNMENT (hàng ký gửi), CONTAINER (hàng đang đi đường)]")
    private String typeReport;
}
