package application.database.services;

import application.database.entities.User;
import application.database.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreateByEmail(String email, String name, String picture) {
        var user = userRepository.findByMail(email)
                .orElseGet(() -> createNewUserFromEmail(email, name, picture));

        if (!Objects.equals(name, "User is not registered")) {
            if (!Objects.equals(user.getNickname(), name)) {
                user.setNickname(name);
            }

            if (!Objects.equals(user.getPicture(), picture)) {
                user.setPicture(picture);
            }
        }

        return userRepository.save(user);
    }

    public User findById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));
    }

    private User createNewUserFromEmail(String email, String nickname, String picture) {
        if (nickname == null) {
            nickname = "Default nickname";
        }

        if (picture == null) {
            picture = "";
        }

        User newUser = User.builder()
                .mail(email)
                .nickname(nickname)
                .picture(picture)
                .build();

        return userRepository.save(newUser);
    }
}
