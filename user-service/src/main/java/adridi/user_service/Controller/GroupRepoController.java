package adridi.user_service.Controller;

import adridi.user_service.DTO.GroupRepoRequest;
import adridi.user_service.DTO.GroupRepoResponse;
import adridi.user_service.DTO.OrganizationDTO;
import adridi.user_service.DTO.UserDTO;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Services.GroupRepoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Group management operations")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
})
public class GroupRepoController {

    private final GroupRepoService groupRepoService;

    @Operation(summary = "Create group", description = "Creates new group")
    @PostMapping("/create-group")
    @PreAuthorize("hasAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<GroupRepoResponse> registerGroupRepo(@RequestBody GroupRepoRequest request) {
        GroupRepo groupRepo = groupRepoService.registerGroupRepo(request);
        return ResponseEntity.ok(mapToResponse(groupRepo));
    }

    @Operation(summary = "Get all groups", description = "Retrieves list of all groups")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<List<GroupRepoResponse>> getAllGroups() {
        List<GroupRepo> groups = groupRepoService.getAllGroups();
        List<GroupRepoResponse> responses = groups.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get group by ID", description = "Retrieves specific group")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<GroupRepoResponse> getGroupById(@PathVariable Long id) {
        GroupRepo group = groupRepoService.getGroupById(id);
        return ResponseEntity.ok(mapToResponse(group));
    }

    private GroupRepoResponse mapToResponse(GroupRepo group) {
        OrganizationDTO orgDto = null;
        if (group.getOrganization() != null) {
            orgDto = OrganizationDTO.builder()
                    .id(group.getOrganization().getId())
                    .org_name(group.getOrganization().getOrg_name())
                    .org_email(group.getOrganization().getOrg_email())
                    .org_owner(group.getOrganization().getOrg_owner())
                    .classRoomId(group.getOrganization().getClassRoom() != null ?
                            group.getOrganization().getClassRoom().getId() : null)
                    .groupRepos(new ArrayList<>())
                    .fieldManagers(group.getOrganization().getFieldManagers().stream()
                            .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail()))
                            .collect(Collectors.toSet()))
                    .build();
        }

        Set<UserDTO> teacherDtos = group.getTeachers().stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toSet());

        return new GroupRepoResponse(
                group.getId(),
                group.getGroup_name(),
                group.getGroup_description(),
                group.getGroup_type(),
                orgDto,
                teacherDtos
        );
    }
}