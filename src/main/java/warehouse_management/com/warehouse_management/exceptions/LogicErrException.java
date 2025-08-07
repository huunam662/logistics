package warehouse_management.com.warehouse_management.exceptions;

import org.springframework.http.HttpStatus;
import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrCode;

public class LogicErrException extends RuntimeException {
    private final String rawMessage;
    private final String messageKey;
    private final Object[] args;
    private final String code;
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;


    private LogicErrException(String code, String messageKey, String rawMessage, Object... args) {
        this.code = code;
        this.messageKey = messageKey;
        this.rawMessage = rawMessage;
        this.args = args;
    }

    public static LogicErrException of(String message, Object... args) {
        return new LogicErrException(null, null, message, args);
    }

    public static LogicErrException ofKey(String messageKey, Object... args) {
        return new LogicErrException(null, messageKey, null, args);
    }

    public static LogicErrException ofCode(String code, Object... args) {
        return new LogicErrException(code, LogicErrCode.getMessageKey(code), null, args);
    }

    // ✅ Trả về chính đối tượng để hỗ trợ chaining
    public LogicErrException setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }
    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

