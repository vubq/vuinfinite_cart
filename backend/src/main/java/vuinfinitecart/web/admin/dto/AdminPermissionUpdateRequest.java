package vuinfinitecart.web.admin.dto;

import lombok.Data;
import java.util.Set;

@Data
public class AdminPermissionUpdateRequest {
    private Set<Long> groupIds;
    private Set<Long> individualPermissionIds;
}
