package toy.board.repository.redis;

import java.util.Optional;

public interface CacheRepository {

    Optional<String> getValues(String key);

    boolean deleteValues(String key);

    void setValues(String key, String value, Long expiredTime);
}
