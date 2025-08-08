package warehouse_management.com.warehouse_management.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class SimpleMapperUtil {

    // Các kiểu cơ bản được map
    private static final Set<Class<?>> SUPPORTED_SIMPLE_TYPES = new HashSet<>();

    static {
        SUPPORTED_SIMPLE_TYPES.add(String.class);
        SUPPORTED_SIMPLE_TYPES.add(Integer.class);
        SUPPORTED_SIMPLE_TYPES.add(int.class);
        SUPPORTED_SIMPLE_TYPES.add(Long.class);
        SUPPORTED_SIMPLE_TYPES.add(long.class);
        SUPPORTED_SIMPLE_TYPES.add(Double.class);
        SUPPORTED_SIMPLE_TYPES.add(double.class);
        SUPPORTED_SIMPLE_TYPES.add(Float.class);
        SUPPORTED_SIMPLE_TYPES.add(float.class);
        SUPPORTED_SIMPLE_TYPES.add(Boolean.class);
        SUPPORTED_SIMPLE_TYPES.add(boolean.class);
        SUPPORTED_SIMPLE_TYPES.add(BigDecimal.class);
        SUPPORTED_SIMPLE_TYPES.add(LocalDateTime.class);
    }

    /**
     * Map các field cơ bản từ source sang target
     */
    public static <S, T> void mapBasicFields(S source, T target) {
        if (source == null || target == null) {
            return;
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Field[] sourceFields = sourceClass.getDeclaredFields();
        for (Field sourceField : sourceFields) {
            if (!SUPPORTED_SIMPLE_TYPES.contains(sourceField.getType())) {
                // Bỏ qua nếu không phải kiểu cơ bản
                continue;
            }

            try {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                Field targetField;
                try {
                    targetField = targetClass.getDeclaredField(sourceField.getName());
                } catch (NoSuchFieldException e) {
                    continue; // Field không tồn tại bên target
                }

                if (SUPPORTED_SIMPLE_TYPES.contains(targetField.getType())) {
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}