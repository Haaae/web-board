package toy.board.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import toy.board.constant.SessionConst;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Configuration
public class LoginInterceptor implements HandlerInterceptor {

    private static final int URL_FROM_INDEX = "http://".length();
    private static final String URL_SLICE = "/";
    private static final String GET = "GET";
    public static final String REGEX_PATICULAR_POST = "/post/[0-9]+";
    public static final String URL_POST_LIST = "/posts";
    public static final String POST = "POST";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        if (isRequestDoesNotNeedCheck(request)) {
            return true;
        }

        validate(request);

        return true;
    }

    private void validate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED);
        }
    }

    private boolean isRequestDoesNotNeedCheck(HttpServletRequest request) {
        String method = request.getMethod();
        String url = getRequestURLWithNoDomain(request);
        return isUrlToGetPostList(method, url)
                || isUrlToGetParticularPost(method, url)
                || isUrlToJoin(method, url);
    }

    private boolean isUrlToJoin(String method, String url) {
        return method.equals(POST) && url.equals("/users");
    }

    private boolean isUrlToGetParticularPost(String method, String url) {
        return method.equals(GET) && url.matches(REGEX_PATICULAR_POST);
    }

    private boolean isUrlToGetPostList(String method, String url) {
        return method.equals(GET) && url.equals(URL_POST_LIST);
    }

    private String getRequestURLWithNoDomain(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        return url.substring(url.indexOf(URL_SLICE, URL_FROM_INDEX));
    }
}
