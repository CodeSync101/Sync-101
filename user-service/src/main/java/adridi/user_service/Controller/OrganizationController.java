package adridi.user_service.Controller;

import adridi.user_service.DTO.OrganizationRequest;
import adridi.user_service.DTO.GroupRepoDTO;
import adridi.user_service.DTO.OrganizationDTO;
import adridi.user_service.DTO.UserDTO;
import adridi.user_service.Models.Organization;
import adridi.user_service.Services.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Organization management operations")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
})
public class OrganizationController {
    private final OrganizationService organizationService;

    @Operation(summary = "Create organization", description = "Creates new organization")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDTO> registerOrganization(@RequestBody OrganizationRequest request) {
        Organization organization = organizationService.registerOrganization(request);
        return ResponseEntity.ok(mapToDto(organization));
    }

    @Operation(summary = "Get all organizations", description = "Retrieves list of all organizations")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FIELD_MANAGER')")
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        List<OrganizationDTO> dtos = organizations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get organization by ID", description = "Retrieves specific organization")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FIELD_MANAGER')")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(mapToDto(organization));
    }

    @Operation(summary = "Assign to classroom", description = "Associates organization with classroom")
    @PutMapping("/{orgId}/classroom/{classRoomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDTO> assignToClassRoom(
            @PathVariable Long orgId,
            @PathVariable Long classRoomId) {
        Organization organization = organizationService.assignToClassRoom(orgId, classRoomId);
        return ResponseEntity.ok(mapToDto(organization));
    }

    private OrganizationDTO mapToDto(Organization organization) {
        List<GroupRepoDTO> groupDtos = organization.getGroupRepos().stream()
                .map(group -> new GroupRepoDTO(
                        group.getId(),
                        group.getGroup_name(),
                        group.getGroup_description(),
                        group.getGroup_type()
                ))
                .collect(Collectors.toList());

        Set<UserDTO> fieldManagerDtos = organization.getFieldManagers().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
                .collect(Collectors.toSet());

        return OrganizationDTO.builder()
                .id(organization.getId())
                .org_name(organization.getOrg_name())
                .org_email(organization.getOrg_email())
                .org_owner(organization.getOrg_owner())
                .classRoomId(organization.getClassRoom() != null ? organization.getClassRoom().getId() : null)
                .groupRepos(groupDtos)
                .fieldManagers(fieldManagerDtos)
                .build();
    }
}