package vuinfinitecart.web.customer.dto;

import lombok.Data;

@Data
public class CustomerProfileUpdateRequest {
    private String name;
    private String phone;
    private String email;
    private String currentPassword;
    private String newPassword;
}
