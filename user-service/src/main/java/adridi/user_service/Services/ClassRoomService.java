package adridi.user_service.Services;

import adridi.user_service.Models.ClassRoom;
import java.util.List;

public interface ClassRoomService {
    ClassRoom createClassRoom(String name);
    List<ClassRoom> getAllClassRooms();
    ClassRoom getClassRoomById(Long id);
    void deleteClassRoom(Long id);
    ClassRoom updateClassRoom(Long id, String name);
}