package toy.board.domain.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.Assert;
import toy.board.domain.base.BaseEntity;
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

    @Builder
    public Login(@NotNull final String encodedPassword) {
        Assert.hasText(encodedPassword, "패스워드가 null이거나 비어있습니다. class: Login.class");

        this.password = encodedPassword;
    }
}
