package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.entity.User;
import tasks.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserRepository userRepository;

    @GetMapping
    public List<User> getUsersExcludingGuests() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != User.Role.GUEST)
                .collect(Collectors.toList());
    }
}
