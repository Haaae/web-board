package toy.board.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.board.entity.Agree;

import toy.board.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authentication extends BaseEntity {
    // 암호화된 값을 위해 모든 varchar값의 길이 설정
    public static final int VARCHAR_LENGTH = 128;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authentication_id")
    private Long id;

    @Column(name = "email", length = VARCHAR_LENGTH, unique = true)
    private String email;

    @Column(name = "cell_phone", length = VARCHAR_LENGTH, unique = true)
    private String cellPhone;

    @Column(name = "birthday", length = VARCHAR_LENGTH)
    private String birthday;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private SEX sex;

    @Column(name = "nation")
    @Enumerated(EnumType.STRING)
    private Nation nation;


    @Column(name = "gether_agree", nullable = false)
    @Enumerated(EnumType.STRING)
    private Agree getherAgree;
}
