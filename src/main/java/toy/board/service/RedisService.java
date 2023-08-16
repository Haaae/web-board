package toy.board.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.repository.RedisRepository;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisService {

    private final RedisRepository redisRepository;

    /**
     *
     * @param key
     * @param values
     * @return return true if values exist in DB and same to parameter with delete it.
     */
    @Transactional
    public boolean deleteIfExistAndSame(final String key, final String values) {
        boolean isDeleted = false;
        Optional<String> findValues = redisRepository.getValues(key);

        if (isEquals(values, findValues)) {
            isDeleted = redisRepository.deleteValues(key);
        }

        return isDeleted;
    }

    private boolean isEquals(final String values, final Optional<String> findValues) {
        return values.equals(findValues.orElse(null));
    }
}
