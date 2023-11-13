package toy.board.service.cache;

public interface CacheService {
    void setValues(String s, String authCode, long authCodeExpirationMillis);

    boolean deleteIfValueExistAndEqualTo(String s, String authCode);

}
