package application.dtos.responses;

import application.dtos.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProjectRolesResponse {

    private final List<RoleDto> data;
}