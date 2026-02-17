package application.dtos.responses;

import application.dtos.GetProjectUsersDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetProjectUsersResponse {
    private final GetProjectUsersDto data;
}