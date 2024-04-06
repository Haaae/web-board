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
    private static final String URL_JOIN = "/users";
    private static final String URL_POST_LIST = "/posts";
    private static final String REGEX_OF_POST_DETAIL_URL = URL_POST_LIST + "/[0-9]+";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String OPTIONS = "OPTIONS";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (isRequestDoesNotNeedCheck(request)) {
            return true;
        }

        validate(request);

        return true;
    }

    private void validate(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_AUTHENTICATION);
        }
    }

    private boolean isRequestDoesNotNeedCheck(final HttpServletRequest request) {
        String method = request.getMethod();
        String url = getRequestURLWithNoDomain(request);
        return isMethodOptions(method)
                || isUrlToGetPostList(method, url)
                || isUrlToGetPostDetail(method, url)
                || isUrlToJoin(method, url);
//                || isUrlToGetPost
    }

    private boolean isMethodOptions(String method) {
        return method.equals(OPTIONS);
    }

    private boolean isUrlToJoin(final String method, final String url) {
        return method.equals(POST) && url.equals(URL_JOIN);
    }

    private boolean isUrlToGetPostDetail(final String method, final String url) {
        return method.equals(GET) && url.matches(REGEX_OF_POST_DETAIL_URL);
    }

    private boolean isUrlToGetPostList(final String method, final String url) {
        return method.equals(GET) && url.equals(URL_POST_LIST);
    }

    private String getRequestURLWithNoDomain(final HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        return url.substring(url.indexOf(URL_SLICE, URL_FROM_INDEX));
    }
}
