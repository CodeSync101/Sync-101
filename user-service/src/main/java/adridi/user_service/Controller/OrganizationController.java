package adridi.user_service.Controller;

import adridi.user_service.DTO.OrganizationRequest;
import adridi.user_service.DTO.GroupRepoDTO;
import adridi.user_service.DTO.OrganizationDTO;
import adridi.user_service.Models.Organization;
import adridi.user_service.Services.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @PostMapping("/create")
    public ResponseEntity<OrganizationDTO> registerOrganization(@RequestBody OrganizationRequest request) {
        Organization organization = organizationService.registerOrganization(request);
        return ResponseEntity.ok(mapToDto(organization));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        List<OrganizationDTO> dtos = organizations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(mapToDto(organization));
    }

    @PutMapping("/{orgId}/classroom/{classRoomId}")
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

        return OrganizationDTO.builder()
                .id(organization.getId())
                .org_name(organization.getOrg_name())
                .org_email(organization.getOrg_email())
                .org_owner(organization.getOrg_owner())
                .classRoomId(organization.getClassRoom() != null ? organization.getClassRoom().getId() : null)
                .groupRepos(groupDtos)
                .build();
    }
}