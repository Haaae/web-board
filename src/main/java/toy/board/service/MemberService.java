package toy.board.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.entity.auth.Login;
import toy.board.entity.user.LoginType;
import toy.board.entity.user.Member;
import toy.board.entity.user.Profile;
import toy.board.entity.user.UserRole;
import toy.board.repository.MemberRepository;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    // entity를 처음 가져와야 하는 상황에서는 entity 객체가 없으므로 ById를 사용해야 한다.
    public Optional<Member> findMember(Long id) {
        // memberRepository.getById(member.getId()) 사용하지 않음
        // findMember()는 메서드 실행 시 DB에 접근하지 않고 프록시 객체를 반환한다.
        return memberRepository.findMemberById(id);

    }

    /*
    DB 중복 검사에 관한 글
    : https://www.inflearn.com/questions/59250/%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94-unique-index-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
     */
    public Member join(String username, String password, String nickname) {

        // username 중복 검사
        validateUsernameDuplication(username);

        // nickname 중복 검사
        validateNicknameDuplication(nickname);

        // Create entity
        Profile profile = Profile.builder().nickname(nickname).build();
        Login login = new Login(passwordEncoder.encode(password));
        Member member = new Member(username, login, profile, LoginType.LOCAL_LOGIN, UserRole.USER);

        // save. cascade로 인해 member만 저장해도 profile과 login이 저장된다.
        // TODO: 동시성 문제는 DataIntegrityViolationException을 ControllerAdvice에서 공통 처리한다.
        memberRepository.save(member);
        return member;
    }


    private void validateUsernameDuplication(String username) {
        // throw custom exception
    }

    private void validateNicknameDuplication(String nickname) {
        // throw custom exception
    }

    public void delete(Long memberId) throws IllegalArgumentException {
        memberRepository.deleteById(memberId);
    }

    public List<Member> getReferences() {
        return memberRepository.findAll();
    }


    // update는 변경감지를 통해 수행한다.

    // CRUD
}
