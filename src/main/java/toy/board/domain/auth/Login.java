package toy.board.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import toy.board.domain.base.BaseEntity;
import toy.board.validator.Validator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Login extends BaseEntity {

    public static final int PASSWORD_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long id;

    @Column(name = "password", nullable = false, length = PASSWORD_LENGTH)
    private String password;

    public Login(@NotNull final String encodedPassword) {
        Validator.hasText(encodedPassword);

        this.password = encodedPassword;
    }
}
