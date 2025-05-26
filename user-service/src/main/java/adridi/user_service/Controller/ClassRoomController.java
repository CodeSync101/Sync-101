package adridi.user_service.Controller;

import adridi.user_service.DTO.ClassRoomRequest;
import adridi.user_service.DTO.ClassRoomResponse;
import adridi.user_service.DTO.OrganizationDTO;
import adridi.user_service.Models.ClassRoom;
import adridi.user_service.Services.ClassRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/classroom")
@RequiredArgsConstructor
public class ClassRoomController {
    private final ClassRoomService classRoomService;

    @PostMapping("/create")
    public ResponseEntity<ClassRoomResponse> createClassRoom(@RequestBody ClassRoomRequest request) {
        ClassRoom classRoom = classRoomService.createClassRoom(request.getName());
        return ResponseEntity.ok(mapToResponse(classRoom));
    }

    @GetMapping
    public ResponseEntity<List<ClassRoomResponse>> getAllClassRooms() {
        List<ClassRoomResponse> responses = classRoomService.getAllClassRooms().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassRoomResponse> getClassRoomById(@PathVariable Long id) {
        ClassRoom classRoom = classRoomService.getClassRoomById(id);
        return ResponseEntity.ok(mapToResponse(classRoom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassRoom(@PathVariable Long id) {
        classRoomService.deleteClassRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassRoomResponse> updateClassRoom(
            @PathVariable Long id,
            @RequestBody ClassRoomRequest request) {
        ClassRoom updated = classRoomService.updateClassRoom(id, request.getName());
        return ResponseEntity.ok(mapToResponse(updated));
    }

    private ClassRoomResponse mapToResponse(ClassRoom classRoom) {
        Set<OrganizationDTO> orgDtos = classRoom.getOrganizations().stream()
                .map(org -> OrganizationDTO.builder()
                        .id(org.getId())
                        .org_name(org.getOrg_name())
                        .org_email(org.getOrg_email())
                        .org_owner(org.getOrg_owner())
                        .classRoomId(classRoom.getId())
                        .groupRepos(new ArrayList<>())
                        .build())
                .collect(Collectors.toSet());

        return ClassRoomResponse.builder()
                .id(classRoom.getId())
                .name(classRoom.getName())
                .organizations(orgDtos)
                .build();
    }
}