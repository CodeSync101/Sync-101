package adridi.user_service.Services;

import adridi.user_service.DTO.GroupRepoRequest;
import adridi.user_service.Models.GroupRepo;

public interface GroupRepoService {
    GroupRepo registerGroupRepo(GroupRepoRequest request);
}