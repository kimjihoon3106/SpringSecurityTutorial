package springSecurityTutorial.springSecurityTutorial.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import springSecurityTutorial.springSecurityTutorial.Model.User;
import springSecurityTutorial.springSecurityTutorial.config.CustomBCryptPasswordEncoder;
import springSecurityTutorial.springSecurityTutorial.config.auth.PrincipalDetails;
import springSecurityTutorial.springSecurityTutorial.repository.UserRepository;

@Service("principalOauth2UserService")
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final CustomBCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public PrincipalOauth2UserService(CustomBCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest : "+userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 했는지 확인가능
        System.out.println("userRequest : "+userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code를 리턴(OAuth-Client라이브러리) -> Access Token요청
        // userRequest 정보 -> loadUser 메서드 호출 -> 구글로부터 회원프로필을 받아와줌.

        System.out.println("getAttributes : " + super.loadUser(userRequest).getAttributes());

        String provider = userRequest.getClientRegistration().getClientId();
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider + "_"+providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            System.out.println("해당 계정으로 구글 로그인을 최초로 시도했습니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        } else {
            System.out.println("해당 계정으로 구글 로그인을 한적이 있음");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
