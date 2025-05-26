package adridi.user_service.Services;

import adridi.user_service.Models.Organization;
import adridi.user_service.DTO.OrganizationRequest;
import java.util.List;
public interface OrganizationService {
    Organization registerOrganization(OrganizationRequest request);
    List<Organization> getAllOrganizations();
    Organization getOrganizationById(Long id);
    Organization assignToClassRoom(Long orgId, Long classRoomId);
}