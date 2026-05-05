package application.dtos.responses;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BasicSuccessResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String path;
    public BasicSuccessResponse(int status, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.path = path;
    }
}
