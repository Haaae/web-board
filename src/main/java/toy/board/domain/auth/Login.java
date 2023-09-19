package toy.board.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;
import toy.board.domain.BaseEntity;
import toy.board.domain.user.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member"})
public class Login extends BaseEntity {

    public static final int PASSWORD_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long id;

    @Column(name = "password", nullable = false, length = PASSWORD_LENGTH)
    private String password;

    @OneToOne(mappedBy = "login")
    private Member member;

    @Builder
    public Login(final String encodedPassword) {
        Assert.hasText(encodedPassword, "password must be not empty. class: Login.class");

        this.password = encodedPassword;
    }

    /**
     * only use in the Member Entity.
     * @param member
     */
    public void changeMember(final Member member) {
        this.member = member;
    }
}
