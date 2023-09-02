package toy.board.resolver;

import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Component
public class PageableVerificationArgumentResolver extends PageableHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return super.supportsParameter(parameter);
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {

        final String pageText = webRequest.getParameter("page");
        final String sizeText = webRequest.getParameter("size");

        try {
            int size = Integer.parseInt(Objects.requireNonNull(sizeText));
            int page = Integer.parseInt(Objects.requireNonNull(pageText));

            if (size <= 0 || page < 0) {
                throw new BusinessException(ExceptionCode.BAD_REQUEST_PAGING_ARG);
            }

        } catch (NumberFormatException e) {
            throw new BusinessException(ExceptionCode.BAD_REQUEST_PAGING_ARG);
        }

        return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    }
}
