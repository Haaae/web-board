package toy.board.service;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.repository.redis.RedisRepository;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class RedisService {

    private final RedisRepository redisRepository;

    /**
     *
     * @param key
     * @param values
     * @return return true if values exist in DB and same to parameter with delete it.
     */
    @Transactional
    public boolean deleteIfValueExistAndEqualTo(final String key, final String values) {
        boolean isDeleted = false;
        Optional<String> findValues = redisRepository.getValues(key);

        if (isEquals(values, findValues)) {
            isDeleted = redisRepository.deleteValues(key);
        }

        return isDeleted;
    }

    @Transactional
    public void setValues(final String key, final String value, final Long expiredTime) {
        redisRepository.setValues(key, value, expiredTime);
    }

    private boolean isEquals(final String values, final Optional<String> findValues) {
        return values.equals(findValues.orElse(null));
    }
}
