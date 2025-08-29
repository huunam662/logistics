package warehouse_management.com.warehouse_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    public static final String SUCCESS_CODE = "S000";
    public static final String FAILD_CODE = "F000";
    public static final String DUPLICATE_CODE = "D000";
    public static final String SUCCESS_MESSAGE = "Thành công";
    public static final String FAIL_MESSAGE = "Thất bại";

    @JsonProperty("code")
    private String code = SUCCESS_CODE;

    @JsonProperty("message")
    private String message = SUCCESS_MESSAGE;

    @JsonProperty("result")
    private T result;

    private ApiResponse(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    //  Thành công
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    //  Thành công
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, result);
    }

    // Thất bại với code mặc định, message
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(FAILD_CODE, message, null);
    }

    // Thêm code custom(nếu k sẽ là F000), message
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    //  Dùng default fail code F000, result
    public static <T> ApiResponse<T> fail(String message, T result) {
        return new ApiResponse<>(FAILD_CODE, message, result);
    }

    // Thêm code custom(nếu k sẽ là F000), message, result
    public static <T> ApiResponse<T> fail(String code, String message, T result) {
        return new ApiResponse<>(code, message, result);
    }
}