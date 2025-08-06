package warehouse_management.com.warehouse_management.exceptions.errormsg;

public final class ValidationErrMsg {

    private ValidationErrMsg() {
        // Prevent instantiation
    }

    public static final String REQUIRED = "validationerr.required";
    public static final String MIN_LENGTH = "validationerr.min_length";
    public static final String MAX_LENGTH = "validationerr.max_length";
    public static final String INVALID_FORMAT = "validationerr.invalid_format";
    public static final String MIN_VALUE = "validationerr.min_value";
    public static final String MAX_VALUE = "validationerr.max_value";
    public static final String INVALID_NUMBER = "validationerr.invalid_number";
    public static final String MAX_SIZE = "validationerr.max_size";
}