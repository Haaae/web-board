package toy.board;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)	// password encoder만 사용하기위해 스프링 시큐리티는 사용안함
public class BoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

	// @CreateBy, @LastModifiedBy를 위한 사용자 전달 함수
	@Bean
	public AuditorAware<String> auditorProvider() {
		// TODO: 2023-07-13 spring security의 session에서 ID 등을 가져와 createBy, lastModifiedBy를 수정할 수 있도록 한다.
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
