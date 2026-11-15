
package hospital;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository = new JobRepository();

    @GetMapping
    public List<Job> getJobs() throws SQLException {
        return jobRepository.getAllJobs();
    }
}
