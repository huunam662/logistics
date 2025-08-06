package warehouse_management.com.warehouse_management.anotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidationValidator.class)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {
    String label() default "";

    boolean required() default false;

    int minLength() default -1;

    int maxLength() default -1;

    String regex() default "";

    double min() default Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;

    double maxSize() default 100; // max file size. Default 5MB

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}