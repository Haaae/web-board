package toy.board.domain.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import toy.board.domain.auth.Login;
import toy.board.domain.auth.SocialLogin;
import toy.board.domain.base.BaseEntity;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder(builderMethodName = "innerBuilder")
public class Member extends BaseEntity {

    public static final int USER_ID_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false,  updatable = false)
    private Long id;

    /**
     * 로컬 가입의 경우 유저의 인증 이메일
     * OAuth2를 사용한 회원가입일 경우 UUID 값이 입력
     */
    @Column(name = "username", length = USER_ID_LENGTH, nullable = false, unique = true)
    private String username;

    @Column(name = "login_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "user_role", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    @JoinColumn(name = "profile_id", nullable = false, unique = true, updatable = false)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_login_id", unique = true)
    private SocialLogin socialLogin;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    @JoinColumn(name = "login_id", unique = true)
    @ToString.Exclude
    private Login login;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public static MemberBuilder builder(
            final String username,
            final Login login,
            final Profile profile,
            final LoginType loginType,
            final UserRole userRole) {

        return innerBuilder()
                .username(username)
                .login(login)
                .profile(profile)
                .loginType(loginType)
                .role(userRole)
                .comments(new ArrayList<>())
                .posts(new ArrayList<>());
    }

    public void changeLogin(@NotNull final Login login) {
        if (login == null) {
            throw new IllegalArgumentException("Login must not be NULL." + this.getClass());
        }

        this.login = login;
    }

    public void updateRole(Member target) {
        validateRoleEach(target);
        target.role = UserRole.ADMIN;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    private void validateRoleEach(Member target) {
        if (this.role != UserRole.MASTER || target.role == UserRole.MASTER) {
            throw new BusinessException(ExceptionCode.ROLE_NOT_EXISTS);
        }
    }

    public boolean hasDeleteRight() {
        return role.isDeleteRight();
    }

}

