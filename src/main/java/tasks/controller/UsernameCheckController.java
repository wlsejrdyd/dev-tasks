package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UsernameCheckController {

    private final UserRepository userRepository;

    @GetMapping("/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return !userRepository.existsByUsername(username);
    }
    
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return !userRepository.existsByEmail(email);
    }
}
