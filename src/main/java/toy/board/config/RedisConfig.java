package toy.board.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisConfig {
    private RedisConnectionFactory redisConnectionFactory;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

/*
 * 순환 참조에 기인한 오류 발생.
 * 순환 참조 아닌듯
 * 흠..
 * ===========
 * RedisConfig가 redisConnectionFactory를 필드멤버로 갖고 생성자로 의존성 주입을 받으면,
 * RedisConfig의 redisConnectionFactory() 메서드로 Bean이 등록되어야 하므로 RedisConfig가 미리 생성되어야 하는
 * 패러독스가 발생
 * => @PostConstruct나 생성자를 통해 redisConnectionFactory를 스프링 빈이 아닌 일반 변수로 초기화하면 됨
 */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(host, port);
//    }

    @PostConstruct
    public void init() {
        this.redisConnectionFactory = new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<?,?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}