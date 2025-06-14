package adridi.user_service.Services;

import adridi.user_service.Models.ClassRoom;
import adridi.user_service.Repositories.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRoomServiceImpl implements ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    @Override
    @Transactional
    public ClassRoom createClassRoom(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Classroom name cannot be empty");
        }
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(name);
        log.debug("Created classroom: {}", name);
        return classRoomRepository.save(classRoom);
    }

    @Override
    public List<ClassRoom> getAllClassRooms() {
        return classRoomRepository.findAll();
    }

    @Override
    public ClassRoom getClassRoomById(Long id) {
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteClassRoom(Long id) {
        ClassRoom classRoom = getClassRoomById(id);
        if (!classRoom.getOrganizations().isEmpty()) {
            throw new RuntimeException("Cannot delete classroom with associated organizations");
        }
        classRoomRepository.deleteById(id);
        log.debug("Deleted classroom: {}", id);
    }

    @Override
    @Transactional
    public ClassRoom updateClassRoom(Long id, String name) {
        ClassRoom classRoom = getClassRoomById(id);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Classroom name cannot be empty");
        }
        classRoom.setName(name);
        log.debug("Updated classroom {} to name: {}", id, name);
        return classRoomRepository.save(classRoom);
    }
}