package toy.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.entity.auth.Login;

public interface LoginRepository extends JpaRepository<Login, Long> {

}
