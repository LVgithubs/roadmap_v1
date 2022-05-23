package site.metacoding.blogv3.web;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.handler.ex.CustomException;
import site.metacoding.blogv3.service.UserService;
import site.metacoding.blogv3.util.UtilValid;
import site.metacoding.blogv3.web.dto.Store;
import site.metacoding.blogv3.web.dto.user.JoinReqDto;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final HttpSession session;

    @GetMapping("/login-form")
    public String loginForm() {
        return "/user/loginForm";
    }

    @GetMapping("/join-form")
    public String joinForm() {
        return "/user/joinForm";
    }

    @GetMapping("join/jusoPopup")
    public String juso() {
        return "/user/jusoPopup";
    }

    @GetMapping("/juso/check")
    public @ResponseBody String check() {
        return Store.roadFullAddr;
    }

    @PostMapping("join/jusoPopup")
    public void addressValue(String roadFullAddr) {
        Store.roadFullAddr = roadFullAddr;
    }// 값을 못넘기고 있어요 ㅠ.....

    @GetMapping("/s/user/{id}/update-form")
    public String userChangeForm(@PathVariable Integer id, Model model) {
        // 권한
        User principal = (User) session.getAttribute("principal");
        if (principal.getId() == id) {
            User userEntity = userService.회원정보보기(id);
            model.addAttribute("user", userEntity);
            return "/user/updateForm";
        } else {
            throw new CustomException("수정할 권한이 없습니다.");
        }
    }

    // ResponseEntity 는 @ResponseBody를 붙이지 않아도 data를 리턴한다.
    @GetMapping("/api/user/username-same-check")
    public ResponseEntity<?> usernameSameCheck(String username) {
        boolean isNotSame = userService.유저네임중복체크(username); // true (같지 않다)
        return new ResponseEntity<>(isNotSame, HttpStatus.OK);
    }

    @PostMapping("/join")
    public String join(@Valid JoinReqDto joinReqDto, BindingResult bindingResult) {

        UtilValid.요청에러처리(bindingResult);
        userService.회원가입(joinReqDto.toEntity());

        return "redirect:/login-form";
    }
}
