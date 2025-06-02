package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tasks.entity.User;
import tasks.entity.User.Role;
import tasks.repository.AttendanceStatusRepository;
import tasks.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final UserRepository userRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        try {
            Role newRole = Role.valueOf(role.toUpperCase());
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
            return ResponseEntity.ok("권한이 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 권한입니다.");
        }
    }

    @Transactional
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        User user = userOpt.get();
        attendanceStatusRepository.deleteByUser(user); // ✅ 연관 근태 상태 먼저 삭제
        userRepository.deleteById(id);

        return ResponseEntity.ok("삭제되었습니다.");
    }
}
