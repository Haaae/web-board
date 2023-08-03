package toy.board.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.board.entity.BaseEntity;
import toy.board.entity.auth.Cidi;
import toy.board.entity.auth.Login;
import toy.board.entity.auth.SocialLogin;

@Entity
@Getter
@Table(catalog = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Transient
    private static final int USER_ID_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    /**
     * 로컬 가입의 경우 유저의 인증 이메일
     * OAuth2를 사용한 회원가입일 경우 UUID 값이 입력
     */
    @Column(name = "username", length = USER_ID_LENGTH, nullable = false, unique = true)
    private String username;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authentication_id", unique = true)
    private Authentication authentication;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_login_id", unique = true)
    private SocialLogin socialLogin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_id", unique = true)
    private Login login;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cidi_id", unique = true)
    private Cidi cidi;

    public void changeLogin(Login login) {
        if (login == null) {
            throw new IllegalArgumentException("login is NULL.");
        }

        this.login = login;
        login.changeMember(this);
    }
}

