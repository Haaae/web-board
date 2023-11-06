package toy.board.config;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import toy.board.constant.SessionConst;

@Configuration
@RequiredArgsConstructor
public class AuditorAwareProvider implements AuditorAware<Long> {

    private final HttpSession session;

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(
                (Long) session.getAttribute(SessionConst.LOGIN_MEMBER)
        );
    }
}
