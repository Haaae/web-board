package toy.board.service.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toy.board.repository.redis.CacheRepository;

class RedisServiceTest {

    private final RedisService redisService =
            new RedisService(
                    new FakeCacheRepository()
            );

    @DisplayName("저장된 key-value와 key value가 모두 같은 경우 return true")
    @Test
    public void deleteIfExistAndSame_success() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;

        redisService.setValues(key, value, expiredTime);

        //when
        boolean result = redisService.deleteIfValueExistAndEqualTo(key, value);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("저장된 key-value 중 key가 다른 경우 return false")
    @Test
    public void deleteIfExistAndSame_fail_cause_wrong_key() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;

        redisService.setValues(key, value, expiredTime);

        //when
        String otherKey = "otherKey";
        boolean result = redisService.deleteIfValueExistAndEqualTo(otherKey, value);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("저장된 key-value 중 value가 다른 경우 return false")
    @Test
    public void deleteIfExistAndSame_fail_cause_wrong_value() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;

        redisService.setValues(key, value, expiredTime);

        //when
        String otherValue = "otherValue";
        boolean result = redisService.deleteIfValueExistAndEqualTo(key, otherValue);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("저장된 key-value 중 key value가 모두 다른 경우 return false")
    @Test
    public void deleteIfExistAndSame_fail_cause_wrong_key_and_wrong_value() throws Exception {
        //given
        String key = "key";
        String value = "value";
        long expiredTime = 1000L;

        redisService.setValues(key, value, expiredTime);

        //when
        String otherKey = "otherKey";
        String otherValue = "otherValue";
        boolean result = redisService.deleteIfValueExistAndEqualTo(otherKey, otherValue);

        //then
        assertThat(result).isFalse();
    }

    private static class FakeCacheRepository implements CacheRepository {

        private final Map<String, Object> template = new HashMap<>();

        @Override
        public Optional<String> getValues(String key) {
            return Optional.ofNullable(
                    (String) template.get(key)
            );
        }

        @Override
        public boolean deleteValues(String key) {
            if (template.containsKey(key)) {
                template.remove(key);
                return true;
            }

            return false;
        }

        @Override
        public void setValues(String key, String value, Long expiredTime) {
            template.put(key, value);
        }
    }
}