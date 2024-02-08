package toy.board.service.cache;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.repository.redis.CacheRepository;

@Service
@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class RedisService implements CacheService {

    private final CacheRepository cacheRepository;

    /**
     * @param key
     * @param values
     * @return return true if values exist in DB and same with parameter to delete it.
     */
    @Transactional
    @Override
    public boolean deleteIfValueExistAndEqualTo(final String key, final String values) {
        boolean isDeleted = false;
        Optional<String> findValues = cacheRepository.getValues(key);

        if (isEquals(values, findValues)) {
            isDeleted = cacheRepository.deleteValues(key);
        }

        return isDeleted;
    }

    @Transactional
    @Override
    public void setValues(final String key, final String value, final long expiredTime) {
        cacheRepository.setValues(key, value, expiredTime);
    }

    private boolean isEquals(final String values, final Optional<String> findValues) {
        return values.equals(findValues.orElse(null));
    }
}
