package adridi.user_service.Controller;

import adridi.user_service.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin operations for user assignments")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
})
public class AdminController {
    private final UserService userService;

    @Operation(summary = "Assign teacher to group", description = "Assigns a user as a teacher to a group")
    @PostMapping("/assign-teacher")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> assignTeacherToGroup(
            @RequestParam Long userId,
            @RequestParam Long groupId) {
        userService.assignTeacherToGroup(userId, groupId);
        return ResponseEntity.ok("Teacher assigned to group");
    }

    @Operation(summary = "Assign field manager to organization", description = "Assigns a user as a field manager to an organization")
    @PostMapping("/assign-field-manager")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> assignFieldManagerToOrganization(
            @RequestParam Long userId,
            @RequestParam Long organizationId) {
        userService.assignFieldManagerToOrganization(userId, organizationId);
        return ResponseEntity.ok("Field manager assigned to organization");
    }
}