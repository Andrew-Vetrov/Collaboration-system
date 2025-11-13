package application.projects;

import application.database.entities.Project;
import application.database.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/projects/{userId}")
    public ProjectBasicDto createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @PathVariable String userId) {
        UUID userUuid = UUID.fromString(userId);
        Project project = projectService.createProject(request, userUuid);
        return projectService.convertToBasicDto(project);
    }

    @GetMapping("/projects/{userId}")
    public List<ProjectBasicDto> getUserProjects(
            @PathVariable String userId) {

        try {
            UUID userUuid = UUID.fromString(userId);
            List<Project> projects = projectService.getUserProjects(userUuid);

            return projects.stream()
                    .map(projectService::convertToBasicDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<String> excH(MethodArgumentNotValidException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
}
