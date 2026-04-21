package vuinfinitecart.web.customer.dto;

import lombok.Builder;
import lombok.Data;
import vuinfinitecart.web.customer.entity.Customer;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerAdminResponse {
    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String phone;
    private Customer.CustomerStatus status;
    private LocalDateTime createdAt;
}
