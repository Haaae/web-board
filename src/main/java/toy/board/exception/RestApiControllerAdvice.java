package toy.board.exception;

import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toy.board.dto.RestResponse;
import toy.board.exception.login.LoginException;

@Slf4j
@RestControllerAdvice
public class RestApiControllerAdvice {

    private static final String VALIDATION_EXCEPTION_MESSAGE = "유효한 데이터가 아닙니다. 타입, 길이 등의 제약사항을 확인바랍니다.";

    /**
     * controller에서 오류가 발생했을 때, 발생한 오류의 클래스타입에 따라 오류 처리 지정
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RestResponse> handleLocalLoginException(CustomException ex) {
        HashMap<String, String> errors = createEmptyErrorMap();
        errors.put(ex.getField(), ex.getDefaultMessage());

        logging(ex.getClass(), ex.getField(), ex.getDefaultMessage());

        return createBadRequestResponseEntityWithErrorsAndMessage(errors, VALIDATION_EXCEPTION_MESSAGE);
    }

    /**
     * Bean Validation 방식의 유효성 평가 중 오류 발생 시 오류 처리.
     * Bean Validation 중 발생한 오류는 자동으로 BindingReslut에 담기므로
     * BindingResult의 모든 필드에러를 RestResponse.object로 반환
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        HashMap<String, String> errors = mapAllErrorsToMap(ex.getAllErrors());

        ex.getAllErrors().forEach(error ->
                logging(
                        error.getClass(),
                        ((FieldError) error).getField(),
                        error.getDefaultMessage()

                )
        );

        return createBadRequestResponseEntityWithErrorsAndMessage(errors, VALIDATION_EXCEPTION_MESSAGE);
    }

    private HashMap<String, String> mapAllErrorsToMap(List<ObjectError> allErrors) {
        HashMap<String, String> errors = createEmptyErrorMap();

        allErrors.forEach(error ->
                errors.put(
                        ((FieldError) error).getField(),
                        error.getDefaultMessage()
                )
        );

        return errors;
    }

    private ResponseEntity<RestResponse> createBadRequestResponseEntityWithErrorsAndMessage(
            HashMap<String, String> errors,
            String message
    ) {
        return ResponseEntity.badRequest().body(
                RestResponse.builder()
                        .success(false)
                        .message(message)
                        .object(errors)
                        .build()
        );
    }

    private HashMap<String, String> createEmptyErrorMap() {
        return new HashMap<>();
    }

    private void logging(final Class clazz, final String field, final String message) {
        log.info("Occurs exception. exception class: {}, field: {}, error message: {}", clazz, field, message);

    }
}
