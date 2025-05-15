package tasks.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.User;
import tasks.repository.UserRepository;
import tasks.service.UserService;

import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", user.getUsername())) {
            model.addAttribute("error", "아이디는 영문, 숫자, 언더스코어(_)만 사용할 수 있습니다.");
            return "signup";
        }

        if (!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\{};':\"\\\\|,.<>/?]).{9,}$", user.getPassword())) {
            model.addAttribute("error", "비밀번호는 9자 이상, 대소문자/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
            return "signup";
        }

        userService.registerUser(user);
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @GetMapping("/find-username")
    public String findUsernamePage() {
        return "find-username";
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "find-password";
    }
}
