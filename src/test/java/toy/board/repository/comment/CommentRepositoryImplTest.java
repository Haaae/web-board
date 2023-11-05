package toy.board.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentRepositoryImplTest {

    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    CommentRepository commentRepository;


}