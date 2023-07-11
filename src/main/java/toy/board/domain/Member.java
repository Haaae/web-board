package toy.board.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    private final int USER_ID_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no", nullable = false)
    private Long no;

    @Column(name = "member_id", unique = true, nullable = false, length = USER_ID_LENGTH)
    private String userId;

    @Column(name = "user_role", nullable = false)
    private UserRole userRole;


}

