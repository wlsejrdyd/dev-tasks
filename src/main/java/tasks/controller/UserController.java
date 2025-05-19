package tasks.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.User;
import tasks.repository.UserRepository;
import tasks.service.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        return "mypage";
    }

    @PostMapping("/mypage")
    public String updateUser(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam(required = false) String currentPassword,
                             @RequestParam(required = false) String newPassword,
                             Principal principal,
                             Model model) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);

        // 비밀번호 변경 요청이 있는 경우
        if (currentPassword != null && !currentPassword.isBlank()
                && newPassword != null && !newPassword.isBlank()) {

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                return "mypage";
            }

            if (newPassword.length() < 9) {
                model.addAttribute("user", user);
                model.addAttribute("error", "새 비밀번호는 9자 이상이어야 합니다.");
                return "mypage";
            }

            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
        model.addAttribute("user", user);
        model.addAttribute("success", "정보가 성공적으로 수정되었습니다.");
        return "mypage";
    }
}
