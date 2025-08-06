package warehouse_management.com.warehouse_management.anotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.exceptions.errormsg.ValidationErrMsg;
import warehouse_management.com.warehouse_management.utils.Msg;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ValidationValidator implements ConstraintValidator<Validation, Object> {

    // Annotation fields
    private String label;
    private boolean required;
    private int minLength;
    private int maxLength;
    private String regex;
    private double min;
    private double max;
    private double maxSize;
    private String message;

    @Override
    public void initialize(Validation constraintAnnotation) {
        this.label = constraintAnnotation.label();
        this.required = constraintAnnotation.required();
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.regex = constraintAnnotation.regex();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.maxSize = constraintAnnotation.maxSize();
        this.message = constraintAnnotation.message();
    }

    /**
     * Main validation logic using a list of functional validators
     */

    @Override
    public boolean isValid(Object data, ConstraintValidatorContext context) {
        if (isRequiredAndEmpty(data)) {
            return buildViolation(context, ValidationErrMsg.REQUIRED);
        }

        String valueStr = String.valueOf(data);

        if (minLength > 0 && valueStr.length() < minLength) {
            return buildViolation(context, ValidationErrMsg.MIN_LENGTH);
        }

        if (maxLength > 0 && valueStr.length() > maxLength) {
            return buildViolation(context, ValidationErrMsg.MAX_LENGTH);
        }

        if (!regex.isEmpty() && !valueStr.matches(regex)) {
            return buildViolation(context, ValidationErrMsg.INVALID_FORMAT);
        }

        // Attempt to parse numeric value only once
        Double numericVal = null;
        if (min != Double.MIN_VALUE || max != Double.MAX_VALUE) {
            try {
                numericVal = Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                return buildViolation(context, ValidationErrMsg.INVALID_NUMBER);
            }
        }

        if (numericVal != null) {
            if (min != Double.MIN_VALUE && numericVal < min) {
                return buildViolation(context, ValidationErrMsg.MIN_VALUE);
            }

            if (max != Double.MAX_VALUE && numericVal > max) {
                return buildViolation(context, ValidationErrMsg.MAX_VALUE);
            }
        }
        return true;
    }

    // ----------------------
    // Individual Validators
    // ----------------------


    private boolean isRequiredAndEmpty(Object data) {
        if (this.required) {
            if (data instanceof List<?> list) {
                return list == null || list.isEmpty();
            }
            return data == null || data.toString().isEmpty();
        }
        return false;

    }


    private boolean buildViolation(ConstraintValidatorContext context, String key) {
        context.disableDefaultConstraintViolation();
        String finalMessage = (message != null && !message.isEmpty())
                ? message
                : switch (key) {
            case ValidationErrMsg.REQUIRED -> Msg.get(key, label);
            case ValidationErrMsg.MIN_LENGTH -> Msg.get(key, label, minLength);
            case ValidationErrMsg.MAX_LENGTH -> Msg.get(key, label, maxLength);
            case ValidationErrMsg.INVALID_FORMAT -> Msg.get(key, label);
            case ValidationErrMsg.MIN_VALUE -> Msg.get(key, label, min);
            case ValidationErrMsg.MAX_VALUE -> Msg.get(key, label, max);
            case ValidationErrMsg.INVALID_NUMBER -> Msg.get(key, label);
            default -> "Validation error for " + label;
        };

        context.buildConstraintViolationWithTemplate(finalMessage)
                .addConstraintViolation();

        return false;
    }

    // ----------------------
    // Error Message Helpers
    // ----------------------


}


