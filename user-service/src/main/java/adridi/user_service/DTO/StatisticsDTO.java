package adridi.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsDTO {
    private long studentCount;
    private long teacherCount;
    private long organizationCount;
    private long classCount;
    private double avgStudentsPerClass;
}