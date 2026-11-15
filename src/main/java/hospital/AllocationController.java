
package hospital;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {

    private final AllocationService allocationService =
        new AllocationService();

    private final StaffRepository staffRepository =
        new StaffRepository();

    private final ResourceRepository resourceRepository =
        new ResourceRepository();

    @GetMapping("/staff")
    public ResponseEntity<?> getStaff() {
        try {
            return ResponseEntity.ok(staffRepository.getAllStaff());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/resources")
    public ResponseEntity<?> getResources() {
        try {
            return ResponseEntity.ok(resourceRepository.getAllResources());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/allocate")
    public ResponseEntity<?> allocate(
            @RequestParam("scheduleId") int scheduleId,
            @RequestParam("jobId") int jobId) {

        try {
            String message = allocationService.allocate(scheduleId, jobId);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/release")
    public ResponseEntity<?> release(
            @RequestParam("scheduleId") int scheduleId,
            @RequestParam("jobId") int jobId) {

        try {
            String message = allocationService.release(scheduleId, jobId);
            return ResponseEntity.ok(Map.of("message", message));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveAllocations() {
        try {
            return ResponseEntity.ok(
                allocationService.getActiveAllocations()
            );
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}