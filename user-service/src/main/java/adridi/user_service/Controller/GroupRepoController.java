package adridi.user_service.Controller;

import adridi.user_service.DTO.GroupRepoRequest;
import adridi.user_service.DTO.GroupRepoResponse;
import adridi.user_service.DTO.OrganizationDTO;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Services.GroupRepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/grouprepo")
@RequiredArgsConstructor
public class GroupRepoController {

    private final GroupRepoService groupRepoService;

    @PostMapping("/creategroup")
    public ResponseEntity<GroupRepoResponse> registerGroupRepo(@RequestBody GroupRepoRequest request) {
        GroupRepo groupRepo = groupRepoService.registerGroupRepo(request);
        GroupRepoResponse response = new GroupRepoResponse(
                groupRepo.getId(),
                groupRepo.getGroup_name(),
                groupRepo.getGroup_description(),
                groupRepo.getGroup_type(),
                new OrganizationDTO(
                        groupRepo.getOrganization().getId(),
                        groupRepo.getOrganization().getOrg_name(),
                        groupRepo.getOrganization().getOrg_email(),
                        groupRepo.getOrganization().getOrg_owner()
                )
        );
        return ResponseEntity.ok(response);
    }
}