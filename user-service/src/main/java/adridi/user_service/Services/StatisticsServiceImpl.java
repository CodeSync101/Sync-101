//package adridi.user_service.Services;
//
//import adridi.user_service.DTO.StatisticsDTO;
//import adridi.user_service.Repositories.UserRepository;
//import adridi.user_service.Repositories.OrganizationRepository;
//import adridi.user_service.Repositories.ClassRoomRepository;
//import adridi.user_service.Services.StatisticsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class StatisticsServiceImpl implements StatisticsService {
//
//    private final UserRepository userRepository;
//    private final OrganizationRepository organizationRepository;
//    private final ClassRoomRepository classRoomRepository;
//
//    @Override
//    public StatisticsDTO getStatistics() {
//        long studentCount = userRepository.countStudents();
//        long teacherCount = userRepository.countTeachers();
//        long organizationCount = organizationRepository.count();
//        long classCount = classRoomRepository.count();
//        double avgStudentsPerClass = classCount > 0 ? (double) studentCount / classCount : 0.0;
//
//        return new StatisticsDTO(
//                studentCount,
//                teacherCount,
//                organizationCount,
//                classCount,
//                avgStudentsPerClass
//        );
//    }
//}