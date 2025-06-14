package adridi.user_service.Services;

import adridi.user_service.DTO.OrganizationRequest;
import adridi.user_service.Models.ClassRoom;
import adridi.user_service.Models.Organization;
import adridi.user_service.Repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final ClassRoomService classRoomService;

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

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public Organization getOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Override
    @Transactional
    public Organization assignToClassRoom(Long orgId, Long classRoomId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        ClassRoom classRoom = classRoomService.getClassRoomById(classRoomId);
        organization.setClassRoom(classRoom);
        classRoom.getOrganizations().add(organization);

        log.debug("Assigned organization {} to classroom {}", orgId, classRoomId);
        return organizationRepository.save(organization);
    }
}