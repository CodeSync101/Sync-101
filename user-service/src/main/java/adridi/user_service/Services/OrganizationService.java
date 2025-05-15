package adridi.user_service.Services;

import adridi.user_service.Models.Organization;
import adridi.user_service.DTO.OrganizationRequest;

public interface OrganizationService {
    Organization registerOrganization(OrganizationRequest request);
}
