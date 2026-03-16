package application.api.users;

import application.database.entities.User;
import application.database.services.UserService;
import application.dtos.responses.ErrorResponse;
import application.dtos.responses.GetUsersMeResponse;
import application.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/users/me")
    public GetUsersMeResponse getUsersMe() throws AuthException {
        UUID userId = jwtService.getCurrentUserId();
        User user = userService.findById(userId);
        return new GetUsersMeResponse(user.getId(), user.getMail(), user.getNickname(), "");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentHandler(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Illegal argument during: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse unauthorizedHandler(AuthException e, HttpServletRequest request) {
        log.warn("Unauthorized request to: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundHandler(EntityNotFoundException e, HttpServletRequest request) {
        log.warn("Entity not found during: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                e.getMessage(), request.getRequestURI());
    }
}
