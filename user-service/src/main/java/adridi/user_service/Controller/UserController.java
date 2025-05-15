package adridi.user_service.Controller;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.DTO.UserResponse;
import adridi.user_service.DTO.GroupRepoDTO;
import adridi.user_service.DTO.UserUpdateRequest;
import adridi.user_service.Models.User;
import adridi.user_service.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        User user = userService.registerUser(request);
        GroupRepoDTO groupRepoDTO = user.getGroup() != null ? new GroupRepoDTO(
                user.getGroup().getId(),
                user.getGroup().getGroup_name(),
                user.getGroup().getGroup_description(),
                user.getGroup().getGroup_type()
        ) : null;

        UserResponse response = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getEmail(),
                user.getPassword(),
                user.getLocked(),
                user.getEnabled(),
                groupRepoDTO
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> responses = users.stream().map(user -> {
            GroupRepoDTO groupRepoDTO = user.getGroup() != null ? new GroupRepoDTO(
                    user.getGroup().getId(),
                    user.getGroup().getGroup_name(),
                    user.getGroup().getGroup_description(),
                    user.getGroup().getGroup_type()
            ) : null;

            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getFirst_name(),
                    user.getLast_name(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getLocked(),
                    user.getEnabled(),
                    groupRepoDTO
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or #id == authentication.principal.attributes['sub']")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        User user = userService.updateUser(id, request);
        GroupRepoDTO groupRepoDTO = user.getGroup() != null ? new GroupRepoDTO(
                user.getGroup().getId(),
                user.getGroup().getGroup_name(),
                user.getGroup().getGroup_description(),
                user.getGroup().getGroup_type()
        ) : null;

        UserResponse response = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getEmail(),
                user.getPassword(),
                user.getLocked(),
                user.getEnabled(),
                groupRepoDTO
        );
        return ResponseEntity.ok(response);
    }
}