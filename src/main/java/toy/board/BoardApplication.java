package toy.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing	// 생성일 수정일 자동으로 기록하기 위한 어노테이션
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)	// password encoder만 사용하기 위해 스프링 시큐리티는 사용 안함 설정
public class BoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}
}
