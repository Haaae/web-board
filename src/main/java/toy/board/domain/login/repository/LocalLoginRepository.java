package toy.board.domain.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.login.entity.auth.LocalLogin;

public interface LocalLoginRepository extends JpaRepository<LocalLogin, Long> {

}
