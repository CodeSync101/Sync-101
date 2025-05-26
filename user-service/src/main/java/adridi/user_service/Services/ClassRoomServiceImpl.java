package adridi.user_service.Services;

import adridi.user_service.Models.ClassRoom;
import adridi.user_service.Repositories.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassRoomServiceImpl implements ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    @Override
    @Transactional
    public ClassRoom createClassRoom(String name) {
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(name);
        return classRoomRepository.save(classRoom);
    }

    @Override
    public List<ClassRoom> getAllClassRooms() {
        return classRoomRepository.findAll();
    }

    @Override
    public ClassRoom getClassRoomById(Long id) {
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassRoom not found"));
    }

    @Override
    @Transactional
    public void deleteClassRoom(Long id) {
        classRoomRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClassRoom updateClassRoom(Long id, String name) {
        ClassRoom classRoom = getClassRoomById(id);
        classRoom.setName(name);
        return classRoomRepository.save(classRoom);
    }
}