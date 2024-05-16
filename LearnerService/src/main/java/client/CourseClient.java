package client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.sliit.LearnerService.dao.dto.CourseDto;

@HttpExchange
public interface CourseClient {
	@GetExchange("/api/v1/courses/public/available")
    public ResponseEntity<List<CourseDto>> getApprovedCourses();
}
