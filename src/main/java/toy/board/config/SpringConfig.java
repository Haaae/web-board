package toy.board.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class SpringConfig {

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    // @CreateBy, @LastModifiedBy를 위한 사용자 전달 함수
    @Bean
    public AuditorAware<String> auditorProvider() {
        // TODO: 2023-07-13 spring security의 session에서 ID 등을 가져와 createBy, lastModifiedBy를 수정할 수 있도록 한다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
