package application.projects;

import application.database.entities.Project;
import application.database.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/projects/{userId}")
    public ProjectBasicDto createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @PathVariable String userId) {

        try {
            UUID userUuid = UUID.fromString(userId);
            Project project = projectService.createProject(request, userUuid);

            return projectService.convertToBasicDto(project);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @GetMapping("/projects/{userId}")
    public List<ProjectBasicDto> getUserProjects(
            @PathVariable String userId) {

        try {
            UUID userUuid = UUID.fromString(userId);
            List<Project> projects = projectService.getUserProjects(userUuid);
            List<ProjectBasicDto> projectDtos = projects.stream()
                    .map(projectService::convertToBasicDto)
                    .collect(Collectors.toList());

            return projectDtos;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}