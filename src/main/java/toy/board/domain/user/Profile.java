package toy.board.domain.user;

import jakarta.persistence.*;
import lombok.*;
import toy.board.domain.base.BaseEntity;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder(builderMethodName = "innerBuilder")
public class Profile extends BaseEntity {

    public static final int NICKNAME_LENGTH = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "nickname", length = NICKNAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    public static ProfileBuilder builder(final String nickname) {
        ProfileBuilder builder = Profile.innerBuilder();

        return builder.nickname(nickname);
    }
}
