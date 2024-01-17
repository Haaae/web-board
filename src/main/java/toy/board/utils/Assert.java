package toy.board.utils;

import org.springframework.util.StringUtils;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

public class Assert {

    public static void notNull(final Object object) {
        if (object == null) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_ARG);
        }
    }

    public static void hasTextAndLength(final String text, final int maxLength) {
        if (!StringUtils.hasText(text) || text.length() > maxLength) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_ARG);
        }
    }

    public static void hasText(final String text) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_ARG);
        }
    }
}
