package warehouse_management.com.warehouse_management.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import warehouse_management.com.warehouse_management.dto.ApiResponse;

import warehouse_management.com.warehouse_management.dto.ValidationErrRes;
import warehouse_management.com.warehouse_management.utils.Msg;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý lỗi validate đầu vào khi dữ liệu từ client DTO không thỏa mãn các ràng buộc
     * (ví dụ: @NotNull, @Size, @Email...).
     * <p>
     * Hàm sẽ trích xuất danh sách các lỗi từ từng field và trả về trong một {@link ApiResponse}
     * chuẩn hóa với danh sách {@link ValidationErrRes}.
     *
     * @param ex ngoại lệ được ném ra khi tham số đầu vào không hợp lệ
     * @return phản hồi chứa danh sách lỗi validation và mã lỗi dạng chuẩn
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ValidationErrRes>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Collect all field error messages from the exception
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        // Optionally, log these errors for debugging purposes
        fieldErrors.forEach(fieldError -> {
            // Using a logger instead of System.out.println for better logging practices
            log.error("Validation failed for field: {}. Message: {}", fieldError.getField(), fieldError.getDefaultMessage());
        });

        // Construct a list of ValidationError objects from the field errors
        List<ValidationErrRes> validationErrors = fieldErrors.stream()
                .map(fieldError -> {
                    return new ValidationErrRes(fieldError.getField(), fieldError.getDefaultMessage());
                })
                .collect(Collectors.toList());

        // Return a custom ApiResponse to return a structured error response
        return ResponseEntity.badRequest().body(ApiResponse.fail("Validation failed", validationErrors));

    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<ValidationErrRes>>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("Validation failed"));
    }

    // Xử lý lỗi logic nghiệp vụ (business logic)
    @ExceptionHandler(LogicErrException.class)
    public ResponseEntity<ApiResponse<?>> handleLogicException(LogicErrException ex) {
        String code = ex.getCode();
        String message = null;

        // 1. Ưu tiên rawMessage (throw bằng of("..."))
        if (ex.getRawMessage() != null && !ex.getRawMessage().isBlank()) {
            message = ex.getRawMessage();
        }
        // 2. Nếu có messageKey (throw bằng ofKey(...))
        else if (ex.getMessageKey() != null && !ex.getMessageKey().isBlank()) {
            message = Msg.get(ex.getMessageKey(), ex.getArgs());
        }
        // Mặc định là BAD_REQUEST, trừ khi exception khai báo khác
        HttpStatus status = ex.getHttpStatus();

        ApiResponse<?> body = (code == null || code.isBlank())
                ? ApiResponse.fail(message)
                : ApiResponse.fail(code, message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Xử lý lỗi @Valid trong @RequestParam, @PathVariable.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());

        log.warn("Constraint violation: {}", errors);
        return ResponseEntity.badRequest().body(ApiResponse.fail("Constraint violation", errors));
    }

    /**
     * Lỗi khi JSON bị sai format, thiếu trường, không parse được request body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("Invalid request body: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail("Invalid request body format."));
    }

    /**
     * Lỗi khi thiếu parameter bắt buộc trong @RequestParam.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String error = "Missing required parameter: " + ex.getParameterName();
        log.warn(error);
        return ResponseEntity.badRequest().body(ApiResponse.fail(error));
    }

    /**
     * Lỗi gọi sai method (GET vs POST, v.v.).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ApiResponse<String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not allowed: {}", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()), "HTTP method not supported."));
    }


    /**
     * Bắt tất cả các lỗi chưa được handle.
     */
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<ApiResponse<String>> handleAllUncaughtException(Exception ex) {
//        logger.error("Unexpected error", ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.fail(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Unexpected server error."));
//    }


}

