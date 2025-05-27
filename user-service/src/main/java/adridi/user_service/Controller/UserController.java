package adridi.user_service.Controller;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.DTO.UserResponse;
import adridi.user_service.DTO.GroupRepoDTO;
import adridi.user_service.DTO.UserUpdateRequest;
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
import java.util.Set;
import java.util.HashSet;
import java.util.List;
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

    @Operation(summary = "Register new user", description = "Creates a new user account")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Get all users", description = "Retrieves list of all users")
    @GetMapping("/all")
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> responses = users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves specific user by their ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Delete user", description = "Removes user from system")
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user", description = "Updates user information")
    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('admin') or #id == authentication.principal.attributes['sub']")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Add user to group", description = "Assigns user to a specific group")
    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<UserResponse> addUserToGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId) {
        User user = userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @Operation(summary = "Remove user from group", description = "Removes user from a specific group")
    @DeleteMapping("/{userId}/groups/{groupName}")
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<UserResponse> removeUserFromGroup(
            @PathVariable Long userId,
            @PathVariable String groupName) {
        User user = userService.removeUserFromGroup(userId, groupName);
        return ResponseEntity.ok(mapToResponse(user));
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

        return UserResponse.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .username(user.getUsername())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .email(user.getEmail())
                .locked(user.getLocked())
                .enabled(user.getEnabled())
                .groups(groupRepoDTOs)
                .build();
    }
}