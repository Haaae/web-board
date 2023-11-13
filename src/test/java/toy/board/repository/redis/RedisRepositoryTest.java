package toy.board.repository.redis;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RedisRepositoryTest {

    @Autowired
    private RedisRepository redisRepository;

    @DisplayName("저장 및 읽기 확인")
    @Test
    public void 저장_읽기_성공() throws Exception {
        //given
        String key = "key";
        String value = "value get success";
        redisRepository.setValues(key, value, 100000L);

        //when
        Optional<String> values = redisRepository.getValues(key);

        //then
        Assertions.assertThat(values.isPresent()).isTrue();
        Assertions.assertThat(values.get()).isEqualTo(value);
    }
}