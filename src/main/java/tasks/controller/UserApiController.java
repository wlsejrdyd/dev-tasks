package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.entity.User;
import tasks.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
