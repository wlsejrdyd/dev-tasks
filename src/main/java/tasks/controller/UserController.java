package tasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/mypage")
    public String myPage() {
        return "user/mypage";  // 이거는 나중에 추가할 수도 있어
    }
}

