package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.DTO.UserUpdateRequest;
import adridi.user_service.Models.User;
import java.util.List;

public interface UserService {
    User registerUser(UserRequest request);
    List<User> getAllUsers();
    void deleteUser(Long id);
    User updateUser(Long id, UserUpdateRequest request);
}