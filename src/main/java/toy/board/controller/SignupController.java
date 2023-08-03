package toy.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import toy.board.dto.login.EmailValidationRequest;
import toy.board.dto.RestResponse;

@RestController("/signup")
public class SignupController {

    /**
     * Validation Email Duplication
     * @param request
     * @return
     * Http Message Body를 객체에 매핑하는 @RequestBody는 객체의 프로퍼티가 하나라도 맞지 않으면 에러가 발생
     * Error Type: MethodArgumentNotValidException.class
     */
    @PostMapping("/validate-email")
    public ResponseEntity<RestResponse> validateEmail(@RequestBody EmailValidationRequest request) {
        // TODO: 2023-07-25 - Have to implement logic




        return ResponseEntity.ok(
                RestResponse.builder()
                        .build()
        );
    }
}
