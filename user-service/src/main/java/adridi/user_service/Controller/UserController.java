package adridi.user_service.Controller;

import adridi.user_service.DTO.*;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Models.Organization;
import adridi.user_service.Models.User;
import adridi.user_service.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management operations")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
})
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get current user", description = "Retrieves information of the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = userService.getOrCreateUserFromJwt();
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Register new user", description = "Creates a new user account with a mandatory organization")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Get all users", description = "Retrieves list of all users")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> responses = users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves specific user by their ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Delete user", description = "Removes user from system")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user", description = "Updates user information")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Add user to group", description = "Assigns user to a specific group")
    @PostMapping("/{userId}/groups/{groupId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> addUserToGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId) {
        User user = userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Remove user from group", description = "Removes user from a specific group")
    @DeleteMapping("/{userId}/groups/{groupName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> removeUserFromGroup(
            @PathVariable Long userId,
            @PathVariable String groupName) {
        User user = userService.removeUserFromGroup(userId, groupName);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Get teacher groups", description = "Retrieves groups the user is responsible for as a teacher")
    @GetMapping("/groups/teacher")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Set<GroupRepoDTO>> getTeacherGroups() {
        Set<GroupRepo> groups = userService.getTeacherGroups();
        Set<GroupRepoDTO> groupDTOs = groups.stream()
                .map(group -> new GroupRepoDTO(
                        group.getId(),
                        group.getGroup_name(),
                        group.getGroup_description(),
                        group.getGroup_type()
                ))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(groupDTOs);
    }

    @Operation(summary = "Get managed organizations", description = "Retrieves organizations the user manages as a field manager")
    @GetMapping("/organizations/field-manager")
    @PreAuthorize("hasAuthority('FIELD_MANAGER')")
    public ResponseEntity<Set<OrganizationDTO>> getManagedOrganizations() {
        Set<Organization> organizations = userService.getManagedOrganizations();
        Set<OrganizationDTO> orgDTOs = organizations.stream()
                .map(org -> OrganizationDTO.builder()
                        .id(org.getId())
                        .org_name(org.getOrg_name())
                        .org_email(org.getOrg_email())
                        .org_owner(org.getOrg_owner())
                        .classRoomId(org.getClassRoom() != null ? org.getClassRoom().getId() : null)
                        .groupRepos(org.getGroupRepos().stream()
                                .map(group -> new GroupRepoDTO(
                                        group.getId(),
                                        group.getGroup_name(),
                                        group.getGroup_description(),
                                        group.getGroup_type()
                                ))
                                .collect(Collectors.toList()))
                        .fieldManagers(org.getFieldManagers().stream()
                                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail()))
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(orgDTOs);
    }

    @Operation(summary = "Get groups by user ID", description = "Retrieves groups associated with a specific user")
    @GetMapping("/{userId}/groups")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<Set<GroupRepoDTO>> getGroupsByUserId(@PathVariable Long userId) {
        Set<GroupRepo> groups = userService.getUserGroups(userId);
        Set<GroupRepoDTO> groupDTOs = groups.stream()
                .map(group -> new GroupRepoDTO(
                        group.getId(),
                        group.getGroup_name(),
                        group.getGroup_description(),
                        group.getGroup_type()
                ))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(groupDTOs);
    }

    @Operation(summary = "Get organizations by user ID", description = "Retrieves organizations associated with a specific user (primary and managed)")
    @GetMapping("/{userId}/organizations")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FIELD_MANAGER')")
    public ResponseEntity<Set<OrganizationDTO>> getOrganizationsByUserId(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        Set<Organization> organizations = new HashSet<>(user.getManagedOrganizations());
        organizations.add(user.getOrganization());
        Set<OrganizationDTO> orgDTOs = organizations.stream()
                .map(org -> OrganizationDTO.builder()
                        .id(org.getId())
                        .org_name(org.getOrg_name())
                        .org_email(org.getOrg_email())
                        .org_owner(org.getOrg_owner())
                        .classRoomId(org.getClassRoom() != null ? org.getClassRoom().getId() : null)
                        .groupRepos(org.getGroupRepos().stream()
                                .map(group -> new GroupRepoDTO(
                                        group.getId(),
                                        group.getGroup_name(),
                                        group.getGroup_description(),
                                        group.getGroup_type()
                                ))
                                .collect(Collectors.toList()))
                        .fieldManagers(org.getFieldManagers().stream()
                                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getEmail()))
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(orgDTOs);
    }

    private UserResponse mapToResponse(User user) {
        Set<GroupRepoDTO> groupRepoDTOs = (user.getGroups() != null)
                ? user.getGroups().stream()
                .map(group -> new GroupRepoDTO(
                        group.getId(),
                        group.getGroup_name(),
                        group.getGroup_description(),
                        group.getGroup_type()
                ))
                .collect(Collectors.toSet())
                : new HashSet<>();

        Set<GroupRepoDTO> teacherGroupDTOs = (user.getTeacherGroups() != null)
                ? user.getTeacherGroups().stream()
                .map(group -> new GroupRepoDTO(
                        group.getId(),
                        group.getGroup_name(),
                        group.getGroup_description(),
                        group.getGroup_type()
                ))
                .collect(Collectors.toSet())
                : new HashSet<>();

        Set<OrganizationDTO> managedOrgDTOs = (user.getManagedOrganizations() != null)
                ? user.getManagedOrganizations().stream()
                .map(org -> OrganizationDTO.builder()
                        .id(org.getId())
                        .org_name(org.getOrg_name())
                        .org_email(org.getOrg_email())
                        .org_owner(org.getOrg_owner())
                        .classRoomId(org.getClassRoom() != null ? org.getClassRoom().getId() : null)
                        .groupRepos(org.getGroupRepos().stream()
                                .map(group -> new GroupRepoDTO(
                                        group.getId(),
                                        group.getGroup_name(),
                                        group.getGroup_description(),
                                        group.getGroup_type()
                                ))
                                .collect(Collectors.toList()))
                        .fieldManagers(org.getFieldManagers().stream()
                                .map(fm -> new UserDTO(fm.getId(), fm.getUsername(), fm.getEmail()))
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet())
                : new HashSet<>();

        OrganizationDTO organizationDTO = OrganizationDTO.builder()
                .id(user.getOrganization().getId())
                .org_name(user.getOrganization().getOrg_name())
                .org_email(user.getOrganization().getOrg_email())
                .org_owner(user.getOrganization().getOrg_owner())
                .classRoomId(user.getOrganization().getClassRoom() != null ? user.getOrganization().getClassRoom().getId() : null)
                .groupRepos(user.getOrganization().getGroupRepos().stream()
                        .map(group -> new GroupRepoDTO(
                                group.getId(),
                                group.getGroup_name(),
                                group.getGroup_description(),
                                group.getGroup_type()
                        ))
                        .collect(Collectors.toList()))
                .fieldManagers(user.getOrganization().getFieldManagers().stream()
                        .map(fm -> new UserDTO(fm.getId(), fm.getUsername(), fm.getEmail()))
                        .collect(Collectors.toSet()))
                .build();

        return UserResponse.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .username(user.getUsername())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .email(user.getEmail())
                .locked(user.getLocked())
                .enabled(user.getEnabled())
                .githubUsername(user.getGithubUsername())
                .roles(user.getRoles() != null ? user.getRoles() : new HashSet<>())
                .groups(groupRepoDTOs)
                .teacherGroups(teacherGroupDTOs)
                .managedOrganizations(managedOrgDTOs)
                .organization(organizationDTO)
                .build();
    }
}