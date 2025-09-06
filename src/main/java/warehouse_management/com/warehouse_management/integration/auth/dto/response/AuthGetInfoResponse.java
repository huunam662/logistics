package warehouse_management.com.warehouse_management.integration.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthGetInfoResponse {
    private Boolean success;
    private UserDTO user;

    @Data
    public static class UserDTO {

        private String id;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("middle_name")
        private String middleName;

        @JsonProperty("last_name")
        private String lastName;

        private String email;
        private String password;

        @JsonProperty("cell_phone")
        private String cellPhone;

        private String address;
        private Boolean disabled;
        private String gender;
        private String birthday;

        @JsonProperty("private_email")
        private String privateEmail;

        @JsonProperty("phone_extension")
        private String phoneExtension;

        @JsonProperty("passport_id")
        private String passportId;

        @JsonProperty("passport_expired")
        private String passportExpired;

        @JsonProperty("id_card_number")
        private String idCardNumber;

        @JsonProperty("id_card_issue_date")
        private String idCardIssueDate;

        private String nationality;
        private String avatar;

        @JsonProperty("family_address_collection")
        private String familyAddressCollection;

        @JsonProperty("relative_info_collection")
        private String relativeInfoCollection;

        @JsonProperty("created_by")
        private String createdBy;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("modified_by")
        private String modifiedBy;

        @JsonProperty("modified_at")
        private String modifiedAt;

        @JsonProperty("social_info_collection")
        private String socialInfoCollection;

        private String birthplace;
        private String religion;
        private String ethnic;
        private String department;
        private String position;

        @JsonProperty("id_card_issue_palce")
        private String idCardIssuePalce;

        @JsonProperty("password_modified_at")
        private String passwordModifiedAt;

        @JsonProperty("employee_code")
        private String employeeCode;

        private String domicile;

        @JsonProperty("personal_tax_code")
        private String personalTaxCode;

        @JsonProperty("passport_date")
        private String passportDate;

        @JsonProperty("passport_place_of_issue")
        private String passportPlaceOfIssue;

        @JsonProperty("avatar_name")
        private String avatarName;

        private String background;

        @JsonProperty("job_description")
        private String jobDescription;

        @JsonProperty("attachment_code")
        private String attachmentCode;

        @JsonProperty("company_phone_number")
        private String companyPhoneNumber;

        @JsonProperty("personal_email")
        private String personalEmail;

        @JsonProperty("emergency_contact")
        private String emergencyContact;

        @JsonProperty("emergency_relate")
        private String emergencyRelate;

        @JsonProperty("emergency_tel")
        private String emergencyTel;

        @JsonProperty("emergency_phone")
        private String emergencyPhone;

        @JsonProperty("emergency_address")
        private String emergencyAddress;

        @JsonProperty("permanent_address")
        private String permanentAddress;

        private String ward;
        private String province;
        private String district;

        @JsonProperty("current_address")
        private String currentAddress;

        @JsonProperty("join_date")
        private String joinDate;

        @JsonProperty("resignation_date")
        private String resignationDate;

        private String signature;

        @JsonProperty("attendance_code")
        private String attendanceCode;

        @JsonProperty("cover_image")
        private String coverImage;

        @JsonProperty("full_name_unaccent")
        private String fullNameUnaccent;

        private String integrity;

        @JsonProperty("social_insurance_number")
        private String socialInsuranceNumber;

        @JsonProperty("health_insurance_number")
        private String healthInsuranceNumber;

        @JsonProperty("health_insurance_place")
        private String healthInsurancePlace;

        @JsonProperty("health_insurance_place_code")
        private String healthInsurancePlaceCode;

        @JsonProperty("social_insurance_place")
        private String socialInsurancePlace;

        @JsonProperty("job_description_path")
        private String jobDescriptionPath;

        @JsonProperty("job_description_name")
        private String jobDescriptionName;

        @JsonProperty("health_height")
        private String healthHeight;

        @JsonProperty("health_weight")
        private String healthWeight;

        @JsonProperty("health_blood")
        private String healthBlood;

        @JsonProperty("health_congenital")
        private String healthCongenital;

        @JsonProperty("healthy_status")
        private String healthyStatus;

        @JsonProperty("health_last_date")
        private String healthLastDate;

        @JsonProperty("no_timekeeping")
        private String noTimekeeping;

        @JsonProperty("extend_user_full_name")
        private String extendUserFullName;

        @JsonProperty("extend_user_name_email")
        private String extendUserNameEmail;

        @JsonProperty("extend_orchart_position")
        private List<Object> extendOrchartPosition;
    }
}