package adridi.user_service.Services;

import adridi.user_service.Repositories.OrganizationRepository;
import adridi.user_service.DTO.OrganizationRequest;
import adridi.user_service.Models.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public Organization registerOrganization(OrganizationRequest request) {
        Organization organization = new Organization(
                request.getOrg_name(),
                request.getOrg_email(),
                request.getOrg_owner()
        );
        return organizationRepository.save(organization);
    }
}
