package toy.board.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import toy.board.domain.base.BaseEntity;
import toy.board.domain.post.Comment;
import toy.board.domain.post.Post;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.validator.Validator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder(builderMethodName = "innerBuilder")
public class Member extends BaseEntity {

    public static final int USER_ID_LENGTH = 50;
    public static final int NICKNAME_LENGTH = 8;
    public static final int PASSWORD_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;

    /**
     * 로컬 가입의 경우 유저의 인증 이메일.
     */
    @Column(name = "username", length = USER_ID_LENGTH, nullable = false, unique = true)
    private String username;

    @Column(name = "user_role", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "nickname", length = NICKNAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false, length = PASSWORD_LENGTH)
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    @ToString.Exclude
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public static MemberBuilder builder(
            @NotNull final String username,
            @NotNull final String nickname,
            @NotNull final String password,
            @NotNull final UserRole userRole) {

        validate(username, nickname, password, userRole);

        return innerBuilder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .role(userRole);
    }

    private static void validate(
            final String username,
            final String nickname,
            final String password,
            final UserRole userRole
    ) {
        Validator.hasTextAndLength(username, USER_ID_LENGTH);
        Validator.hasTextAndLength(nickname, NICKNAME_LENGTH);
        Validator.hasTextAndLength(password, PASSWORD_LENGTH);

        Validator.notNull(userRole);
    }

    public void updateRole(Member target) {
        validateRoleEach(target);
        target.role = UserRole.ADMIN;
    }

    public void addPost(final Post post) {
        this.posts.add(post);
    }

    public void addComment(final Comment comment) {
        this.comments.add(comment);
    }

    public boolean hasDeleteRight() {
        return role.isDeleteRight();
    }

    /**
     * 유저 탈퇴 시 호출하여 작성했던 게시글과 댓글의 작성자를 null로 만드는 기능
     */
    public void changeAllPostAndCommentWriterToNull() {
        this.posts.forEach(Post::applyWriterWithdrawal);
        this.comments.forEach(Comment::applyWriterWithdrawal);
    }

    private void validateRoleEach(final Member target) {
        if (this.role != UserRole.MASTER || target.role == UserRole.MASTER) {
            throw new BusinessException(ExceptionCode.INVALID_AUTHORITY);
        }
    }

    public List<Post> getPosts() {
        return Collections.unmodifiableList(this.posts);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(this.comments);
    }

    public long getPostCount() {
        return this.posts.size();
    }

    public long getCommentCount() {
        return this.comments.stream()
                .filter(c -> !c.isDeleted())
                .count();
    }
}