package toy.board.service.post.dto;

import java.util.HashMap;
import java.util.Map;
import toy.board.repository.comment.dto.CommentListDto;
import toy.board.repository.post.dto.PostDto;

public record PostDetailDto(
        Map<String, Object> postDetail
) {

    public static PostDetailDto of(PostDto postDto, CommentListDto commentListDto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("post", postDto);
        map.put("comments", commentListDto.commentDtos());
        return new PostDetailDto(map);
    }
}
