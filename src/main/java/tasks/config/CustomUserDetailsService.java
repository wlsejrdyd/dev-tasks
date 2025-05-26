package tasks.config;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import tasks.entity.User;
import tasks.entity.AttendanceStatus;
import tasks.repository.UserRepository;
import tasks.repository.AttendanceStatusRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    AttendanceStatusRepository attendanceStatusRepository) {
        this.userRepository = userRepository;
        this.attendanceStatusRepository = attendanceStatusRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // leaveDate가 지정된 사용자는 로그인 차단
        AttendanceStatus status = attendanceStatusRepository.findByUser(user).orElse(null);
        if (status != null && status.getLeaveDate() != null) {
            throw new DisabledException("퇴사한 계정입니다.");
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
