package toy.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import toy.board.dto.login.JoinRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// 사용하려면 security test 라이브러리 등록해야 함


@ExtendWith(MockitoExtension.class) // Mockito와 같은 확장 기능을 테스트에 통합시켜주는 어노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc // controller뿐만 아니라 service와 repository 등의 컴포넌트도 mock으로 올린다.
//@WebMvcTest // controller만 mock으로 올림
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void join_success() throws Exception {
        //given
        ObjectMapper mapper = new ObjectMapper();
        JoinRequest joinRequest = new JoinRequest("username@gmail.com", "password1!", "nickname");
        String content = mapper.writeValueAsString(joinRequest);

        System.out.println("content = " + content);
        System.out.println("=====================================");

        // then
        mockMvc.perform(post("/users").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))

                .andExpect(MockMvcResultMatchers.status().isCreated())

                .andDo(MockMvcResultHandlers.print());
    }


}