package application.database.services;

import application.database.entities.User;
import application.database.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreateByEmail(String email, String name) {
        return userRepository.findByMail(email)
                .orElseGet(() -> createNewUserFromEmail(email, name));
    }

    public User findById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));
    }

    private User createNewUserFromEmail(String email, String nickname) {
        if (nickname == null) {
            nickname = "nick name";
        }

        User newUser = User.builder()
                .mail(email)
                .nickname(nickname)
                .build();

        return userRepository.save(newUser);
    }
}
