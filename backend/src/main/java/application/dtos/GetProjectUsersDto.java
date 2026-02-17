package application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetProjectUsersDto {
    private final UUID project_id;
    private final List<ProjectUserDto> users;
}
