package toy.board.controller.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.constant.SessionConst;
import toy.board.controller.api.response.annotation.common.ApiAuthorityError;
import toy.board.controller.api.response.annotation.common.ApiBadRequestArgError;
import toy.board.controller.api.response.annotation.common.ApiFoundError;
import toy.board.controller.api.response.annotation.member.ApiAuthenticationError;
import toy.board.controller.user.dto.request.RolePromotionRequest;
import toy.board.service.member.MemberRoleService;

@Tag(name = "Role", description = "Role API Document")
@Controller
@RequestMapping("/users")
@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RoleController {

    private final MemberRoleService roleService;

    @ApiResponse(
            responseCode = "200",
            description = "권한 변경 성공"
    )
    @ApiBadRequestArgError
    @ApiAuthenticationError
    @ApiAuthorityError
    @ApiFoundError
    @Operation(summary = "권한 변경", description = "사용자의 권한을 변경합니다.")
    @PostMapping("/roles/promotion")
    public ResponseEntity promoteRole(
            @RequestBody @Valid final RolePromotionRequest rolePromotionDto,
            final HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);
        Long masterId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);

        roleService.promoteMemberRole(
                masterId,
                rolePromotionDto.id()
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
