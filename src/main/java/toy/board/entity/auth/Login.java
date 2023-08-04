package toy.board.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;
import toy.board.entity.BaseEntity;
import toy.board.entity.user.Member;

@Entity
//@Table(catalog = "auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member"})
public class Login extends BaseEntity {

    @Transient
    private static final int SOLT_LENGTH = 128;
    @Transient
    private static final int PASSWORD_LENGTH = 128;

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
    public void changeMember(Member member) {
        this.member = member;
    }

//    @Column(name = "solt", nullable = false, length = SOLT_LENGTH)
//    private String solt;
}
