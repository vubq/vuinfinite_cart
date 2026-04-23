package vuinfinitecart.web.customer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
}
