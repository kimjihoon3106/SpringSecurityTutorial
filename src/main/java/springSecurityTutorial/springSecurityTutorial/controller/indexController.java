package springSecurityTutorial.springSecurityTutorial.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springSecurityTutorial.springSecurityTutorial.Model.User;
import springSecurityTutorial.springSecurityTutorial.config.auth.PrincipalDetails;
import springSecurityTutorial.springSecurityTutorial.repository.UserRepository;


@Controller
public class indexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails userDetails) {
        System.out.println("/test/login =======================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principalDetails.getUser()); // Authentication을 DI해서 UserObject를 찾을 수 있고
        System.out.println("userDetails : " + userDetails.getUser()); // @AuthenticationPrincipal 어노테이션을 통해 UserObject를 찾을 수 있다.

        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication,
                                               @AuthenticationPrincipal OAuth2User oauth) { // DI
        System.out.println("/test/oauth.login =======================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("oAuth2User : " + oAuth2User.getAttributes());
        System.out.println("oauth2User : " + oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"", "/"})
    public String index() {
        // 머스테치 기본폴더 src/main/resources/
        // 뷰 리졸버 설정 templates (prefix), .mustache (suffix) 생략가능
        return "index";
    }
    // OAuth 로그인을 해도 PrincipalDetails
    // 일반 로그인을 해도 PrincipalDetails

    // public class PrincipalDetails implements UserDetails, OAuth2User <- 그렇다
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("PrincipalDetails : " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // 스프링 시큐리티가 해당 주소를 낚아 채버린다 -> SecurityConfig 파일을 생성 후 작동안함.
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }
    @Transactional
    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");
        // 시큐리티 로그인을 하려면 패스워드가 암호화되어 있어야함
        // 패스워드를 암호화
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        System.out.println(user);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/joinProc")
    public @ResponseBody String joinProc() {
        return "회원가입 완료";
    }

    @Secured("ROLE_MANAGER")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    //  여러개를 걸고 싶으면
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String dat() {
        return "데이터정보";
    }


}
