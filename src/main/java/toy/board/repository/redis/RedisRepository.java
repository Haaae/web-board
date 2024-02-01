package toy.board.repository.redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisRepository implements CacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<String> getValues(final String key) {
        return Optional.ofNullable(
                (String) redisTemplate
                        .opsForValue()
                        .get(key)
        );
    }

    @Override
    public boolean deleteValues(final String key) {
        return Boolean.TRUE
                .equals(
                        redisTemplate.delete(key)
                );
    }

    @Override
    public void setValues(final String key, final String value, final Long expiredTime) {
        redisTemplate.opsForValue()
                .set(
                        key,
                        value,
                        expiredTime,
                        TimeUnit.MILLISECONDS
                );
    }
}
