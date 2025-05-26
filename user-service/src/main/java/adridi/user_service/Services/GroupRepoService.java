package adridi.user_service.Services;

import adridi.user_service.DTO.GroupRepoRequest;
import adridi.user_service.Models.GroupRepo;

import java.util.List;

public interface GroupRepoService {
    GroupRepo registerGroupRepo(GroupRepoRequest request);
    List<GroupRepo> getAllGroups();
    GroupRepo getGroupById(Long id);
}