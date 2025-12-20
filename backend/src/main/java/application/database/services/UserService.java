package application.database.services;

import application.database.entities.User;
import application.database.repositories.UserRepository;
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
    public User findOrCreateByEmail(String email) {
        return userRepository.findByMail(email)
                .orElseGet(() -> createNewUserFromEmail(email));
    }

    private User createNewUserFromEmail(String email) {
        String nickname = "nick name";

        User newUser = User.builder()
                .mail(email)
                .nickname(nickname)
                .build();

        return userRepository.save(newUser);
    }
}
