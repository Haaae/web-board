package toy.board.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.board.entity.user.Member;
import toy.board.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

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

    public List<Member> getReferences() {
        return memberRepository.findAll();
    }

    public void delete(Member member) {
        // TODO: 2023-08-03 Have to implement detail logic
        // - member
        memberRepository.delete(member);
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    // update는 변경감지를 통해 수행한다.

    // CRUD
}
