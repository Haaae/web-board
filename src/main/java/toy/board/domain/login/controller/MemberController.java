package toy.board.domain.login.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.domain.login.repository.MemberRepository;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)  // @Autowired는 생성자가 한 개일 때 생략할 수 있음
public class MemberController {

    final MemberRepository memberRepository;

//    @PostMapping("/signup")
//    public String create(SignupRequest signupRequest) {
//        /*  회원가입시 필요한 정보
//
//            필수 정보
//                - ID(Login.class)
//                - Password(Login.class)
//                - Nickname(Profile.class)
//
//            선택 입력 정보
//                - 본인인증 정보
//
//         */
//
//        Member member = Member
//                .builder()
//                .localLogin(
//                        LocalLogin
//                                .builder()
//                                .userId(signupRequest.getId())
//                                .password(
//                                        new BCryptPasswordEncoder().encode(signupRequest.getPassword())
//                                )
//                                .build()
//                )
//                .profile(Profile.builder().nickname(signupRequest.getNickname()).build())
//                .role(UserRole.ADMIN)
//                .build();
//
//        memberRepository.save(member);
//
//        return "";
//    }
}
