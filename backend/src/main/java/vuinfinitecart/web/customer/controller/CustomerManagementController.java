package vuinfinitecart.web.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuinfinitecart.web.customer.dto.CustomerAdminResponse;
import vuinfinitecart.web.customer.entity.Customer;
import vuinfinitecart.web.customer.service.CustomerManagementService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/crm")
@RequiredArgsConstructor
public class CustomerManagementController {

    private final CustomerManagementService customerManagementService;

    @GetMapping("/customers")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('customers:read')")
    public List<CustomerAdminResponse> getAllCustomers() {
        return customerManagementService.getAllCustomers();
    }

    @PatchMapping("/customers/{id}/status")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('customers:ban')")
    public void updateCustomerStatus(@PathVariable Long id, @RequestParam Customer.CustomerStatus status) {
        customerManagementService.updateCustomerStatus(id, status);
    }
}
