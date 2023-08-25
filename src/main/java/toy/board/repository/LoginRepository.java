package toy.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.auth.Login;

public interface LoginRepository extends JpaRepository<Login, Long> {

}
