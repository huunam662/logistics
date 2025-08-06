package warehouse_management.com.warehouse_management.dto.warehouse.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import warehouse_management.com.warehouse_management.annotation.Validation;

@Data
@Getter
@Setter
@AllArgsConstructor
public class TestValidationAnnotationRequestDto {

    @Validation(
            label = "username",
            required = true,
            minLength = 5,
            maxLength = 10,
            regex = "^[a-zA-Z]+$" // Chỉ chữ cái
    )
    private String username;

    @Validation(
            label = "age",
            required = true,
            min = 18,
            max = 60
    )
    private Integer age;

    @Validation(
            label = "phone",
            required = true,
            regex = "^[0-9]{10}$"
    )
    private String phone;

    @Validation(
            label = "description",
            required = true,
            minLength = 3,
            maxLength = 5
    )
    private String description;

    @Validation(
            label = "income",
            required = true,
            regex = "^[0-9]+$", // Phải là số
            min = 1000,
            max = 10000
    )
    private String income;

    // Getters/setters hoặc Lombok @Data
}