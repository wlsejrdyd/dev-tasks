package tasks.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.service.MailService;
import tasks.service.UserService;
import tasks.repository.UserRepository;
import tasks.entity.User;

import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MailService mailService;

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

    @PostMapping("/find-username")
    @ResponseBody
    public String findUsername(@RequestParam String email) {
        String username = userService.findUsernameByEmail(email);
        if (username != null) {
            mailService.sendEmail(email, "[TASKS] 아이디 찾기 결과", "회원님의 아이디는: " + username);
            return "아이디가 이메일로 전송되었습니다.";
        } else {
            return "등록된 사용자를 찾을 수 없습니다.";
        }
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "find-password";
    }

    @PostMapping("/find-password")
    @ResponseBody
    public String findPassword(@RequestParam String username, @RequestParam String email) {
        boolean valid = userService.verifyUser(username, email);
        if (!valid) {
            return "일치하는 사용자가 없습니다.";
        }

        String tempPassword = userService.generateTempPassword();
        userService.updatePassword(username, tempPassword);
        mailService.sendEmail(email, "[TASKS] 임시 비밀번호 발급",
                "임시 비밀번호는 다음과 같습니다:\n\n" + tempPassword + "\n\n로그인 후 꼭 비밀번호를 변경해주세요.");
        return "임시 비밀번호가 이메일로 전송되었습니다.";
    }
}
