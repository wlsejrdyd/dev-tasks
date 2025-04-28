package tasks.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.model.User;
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
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            session.setAttribute("user", optionalUser.get());
            session.setMaxInactiveInterval(600); // 10분
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", user.getUsername())) {
            model.addAttribute("error", "아이디는 영문, 숫자, 언더스코어(_)만 사용할 수 있습니다.");
            return "register";
        }
        if (!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{9,}$", user.getPassword())) {
            model.addAttribute("error", "비밀번호는 9자 이상, 대소문자/숫자/특수문자를 각각 1개 이상 포함해야 합니다.");
            return "register";
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

    @PostMapping("/find-username")
    public String findUsername(@RequestParam String email, Model model) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            model.addAttribute("foundUsername", user.get().getUsername());
        } else {
            model.addAttribute("error", "해당 이메일로 등록된 아이디가 없습니다.");
        }
        return "find-username";
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String username, @RequestParam String email, Model model) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getEmail().equals(email)) {
            model.addAttribute("foundPassword", "등록된 이메일로 비밀번호 재설정 링크를 보내드렸습니다. (기능 구현 예정)");
        } else {
            model.addAttribute("error", "입력한 정보와 일치하는 계정이 없습니다.");
        }
        return "find-password";
    }
}
