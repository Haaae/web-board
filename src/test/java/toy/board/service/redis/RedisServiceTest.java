package toy.board.service.redis;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import toy.board.repository.redis.RedisRepository;
import toy.board.service.redis.RedisService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class RedisServiceTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private RedisRepository redisRepository;

    private String key = "key";
    private String value = "value";
    private String otherKey = "otherKey";
    private String otherValue = "otherValue";

    @BeforeEach
    public void init() {
        given(redisRepository.deleteValues(eq(key))).willReturn(true);
        given(redisRepository.deleteValues(eq(otherKey))).willReturn(false);
        given(redisRepository.getValues(anyString())).willReturn(Optional.of(value));
    }

    @DisplayName("저장된 key-value와 key value가 모두 같은 경우 return true")
    @Test
    public void deleteIfExistAndSame_success() throws Exception {
        boolean result = redisService.deleteIfValueExistAndEqualTo(key, value);
        assertThat(result).isTrue();
    }

    @DisplayName("저장된 key-value 중 key가 다른 경우 return false")
    @Test
    public void deleteIfExistAndSame_fail_cause_wrong_key() throws  Exception {
        boolean result = redisService.deleteIfValueExistAndEqualTo(otherKey, value);
        assertThat(result).isFalse();
    }

    @DisplayName("저장된 key-value 중 value가 다른 경우 return false")
    @Test
    public void deleteIfExistAndSame_fail_cause_wrong_value() throws  Exception {
        boolean result = redisService.deleteIfValueExistAndEqualTo(key, otherValue);
        assertThat(result).isFalse();
    }

    @DisplayName("저장된 key-value 중 key value가 모두 다른 경우 return false")
    @Test
    public void deleteIfExistAndSame_fail_cause_wrong_key_and_wrong_value() throws  Exception {
        boolean result = redisService.deleteIfValueExistAndEqualTo(otherKey, otherValue);
        assertThat(result).isFalse();
    }
}