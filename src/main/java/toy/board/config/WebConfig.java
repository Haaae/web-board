package toy.board.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.board.interceptor.LoginInterceptor;
import toy.board.resolver.PageableVerificationArgumentResolver;

@Configuration
@lombok.RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PageableVerificationArgumentResolver pageableVerificationArgumentResolver;
    @Value("${web.security.origin-pattern}")
    private String originPattern;
    @Value("${web.security.max-age}")
    private Long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(originPattern)
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(maxAge);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/users/login",
                        "/users/usernames/**",
                        "/users/nicknames/**",
                        "/users/emails/**/**",

                        // -- Static resources
                        "/css/**", "/images/**", "/js/**"
                        // -- Swagger UI v2
                        , "/v2/api-docs", "/swagger-resources/**"
                        , "/swagger-ui.html", "/webjars/**", "/swagger/**"
                        // -- Swagger UI v3 (Open API)
                        , "/v3/api-docs/**", "/swagger-ui/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableVerificationArgumentResolver);
    }

}
