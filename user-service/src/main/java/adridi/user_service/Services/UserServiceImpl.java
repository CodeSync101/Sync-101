package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.DTO.UserUpdateRequest;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Models.User;
import adridi.user_service.Repositories.GroupRepoRepository;
import adridi.user_service.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GroupRepoRepository groupRepoRepository;
    private final GitHubService gitHubService;

    @Override
    @Transactional
    public User registerUser(UserRequest request) {
        User user = new User(
                null,
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getLocked() != null ? request.getLocked() : false,
                request.getEnabled() != null ? request.getEnabled() : true
        );
        user.setGroups(new HashSet<>());

        if (request.getGroup_name() != null && !request.getGroup_name().isEmpty()) {
            GroupRepo group = groupRepoRepository.findByGroup_name(request.getGroup_name())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            user.getGroups().add(group);
            group.getUsers().add(user);
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getFirstName() != null) user.setFirst_name(request.getFirstName());
        if (request.getLastName() != null) user.setLast_name(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        if (request.getLocked() != null) user.setLocked(request.getLocked());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        if (request.getGroup_name() != null) {
            GroupRepo group = groupRepoRepository.findByGroup_name(request.getGroup_name())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            user.getGroups().add(group);
            group.getUsers().add(user);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User addUserToGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupRepo group = groupRepoRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        try {
            gitHubService.inviteUserToRepo(
                    group.getOrganization().getOrg_owner(),
                    group.getGroup_name(),
                    user.getEmail()
            );

            user.getGroups().add(group);
            group.getUsers().add(user);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add user to group: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public User removeUserFromGroup(Long userId, String groupName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GroupRepo group = groupRepoRepository.findByGroup_name(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        user.getGroups().remove(group);
        group.getUsers().remove(user);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GroupRepo> getUserGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getGroups();
    }
}