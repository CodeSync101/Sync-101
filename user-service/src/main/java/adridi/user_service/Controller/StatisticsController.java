//package adridi.user_service.Controller;
//
//import adridi.user_service.DTO.StatisticsDTO;
//import adridi.user_service.Services.StatisticsService;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/statistics")
//@RequiredArgsConstructor
//public class StatisticsController {
//
//    private final StatisticsService statisticsService;
//
//    @Operation(summary = "Get statistics", description = "Get students, teachers, organizations, classes, and average students per class")
//    @GetMapping
//    public ResponseEntity<StatisticsDTO> getStatistics() {
//        return ResponseEntity.ok(statisticsService.getStatistics());
//    }
//}