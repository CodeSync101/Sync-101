package adridi.user_service.Controller;


import adridi.user_service.DTO.OrganizationRequest;
import adridi.user_service.Models.Organization;
import adridi.user_service.Services.OrganizationService;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping("/create")
    public ResponseEntity<Organization> registerOrganization(@RequestBody OrganizationRequest request) {
        Organization organization = organizationService.registerOrganization(request);
        return ResponseEntity.ok(organization);
    }
}
