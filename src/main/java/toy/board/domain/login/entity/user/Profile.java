package toy.board.domain.login.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import lombok.NoArgsConstructor;
import toy.board.BaseEntity;

@Entity
@Table(catalog = "user")
@Getter
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    protected Profile() {
        // 사용자가 입력하지 않았을 때 기본값을 설정
        this.imageUrl = DEFAULT_URL;
        this.introduction = DEFAULT_INTRODUCTION;
    }

    @Transient
    private static final int IMAGE_URL_LENGTH = 100;
    @Transient
    private static final int INTRODUCTION_LENGTH = 300;
    @Transient
    private static final int NICKNAME_LENGTH = 12;
    @Transient
    private static final String DEFAULT_URL = "";  // 수정 필요
    @Transient
    private static final String DEFAULT_INTRODUCTION = "";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "nickname", length = NICKNAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    @Column(name = "image_url", length = IMAGE_URL_LENGTH, nullable = false)
    private String imageUrl;

    @Column(name = "introduction", length = INTRODUCTION_LENGTH, nullable = false)
    private String introduction;
}
