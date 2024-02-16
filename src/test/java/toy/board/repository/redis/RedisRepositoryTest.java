package toy.board.repository.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
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

    @DisplayName("정상동작 : 저장 및 읽기 확인")
    @Test
    public void 저장_읽기_정상동작() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;
        redisRepository.setValues(key, value, expiredTime);

        //when
        Optional<String> values = redisRepository.getValues(key);

        //then
        assertThat(values.isPresent()).isTrue();
        assertThat(values.get()).isEqualTo(value);
    }

    @DisplayName("정상동작 : 저장 시간 초과 시 저장 내용 삭제")
    @Test
    public void 저장시간초과하면_읽기_실패() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;
        redisRepository.setValues(key, value, expiredTime);

        //when
        Thread.sleep(expiredTime);
        Optional<String> values = redisRepository.getValues(key);

        //then
        assertThat(values.isPresent()).isFalse();
    }

    @DisplayName("정상동작 : 삭제")
    @Test
    void 저장값_삭제() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;
        redisRepository.setValues(key, value, expiredTime);

        //when
        redisRepository.deleteValues(key);
        Optional<String> values = redisRepository.getValues(key);

        //then
        assertThat(values.isPresent()).isFalse();
    }
}