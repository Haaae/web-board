package toy.board.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, String value,Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param key
     * @param values
     * @return return true if values exist in DB and same to parameter with delete it.
     */
    public boolean deleteIfExistAndSame(final String key, final String values) {
        Optional<String> findValues = getValues(key);
        deleteValues(key);  // 어차피 존재하지 않으면 아무 일도 일어나지 않으므로 시행
        return values.equals(findValues.orElse(null));  // if found, the find values must not be null.
    }

    public Optional<String> getValues(final String key){
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(key));
    }

    public void deleteValues(String key){
        redisTemplate.delete(key);
    }


}
