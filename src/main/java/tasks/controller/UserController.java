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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;  // Optional<User>용
import org.springframework.dao.DataIntegrityViolationException;  // 예외 처리용
import org.springframework.security.core.context.SecurityContextHolder;  // 로그아웃 처리용

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
                             @RequestParam(required = false) String newPasswordConfirm,
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

            if (!newPassword.equals(newPasswordConfirm)) {
                model.addAttribute("user", user);
                model.addAttribute("error", "새 비밀번호가 일치하지 않습니다.");
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

    @PostMapping("/mypage/delete")
    public String deleteAccount(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            String username = principal.getName();
            Optional<User> optionalUser = userRepository.findByUsername(username);
    
            if (optionalUser.isPresent()) {
                try {
                    User user = optionalUser.get();
                    userRepository.delete(user);
                    SecurityContextHolder.clearContext(); // 로그아웃 처리
                    return "redirect:/login?logout";
                } catch (DataIntegrityViolationException e) {
                    // 근태 관리에 등록된 경우 FK 제약으로 삭제 실패
                    redirectAttributes.addFlashAttribute("error", "근태 관리에 등록된 계정입니다. 삭제가 불가하오니 관리자에게 문의하십시오.");
                    return "redirect:/mypage";
                }
            }
        }
    
        redirectAttributes.addFlashAttribute("error", "사용자를 찾을 수 없습니다.");
        return "redirect:/mypage";
    }
}
