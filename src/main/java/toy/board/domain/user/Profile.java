package toy.board.domain.user;

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
public class Profile extends BaseEntity {

    public static final int NICKNAME_LENGTH = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "nickname", length = NICKNAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    public Profile(@NotNull final String nickname) {
        Validator.hasTextAndLength(nickname, NICKNAME_LENGTH);

        this.nickname = nickname;
    }
}
