package toy.board.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommentType {
    COMMENT, REPLY;

    // TODO: 2023-08-29 enum이 dto에 잘 변환되는지 테스트
    @JsonCreator
    public static CommentType from(String s) {
        return CommentType.valueOf(s.toUpperCase());
    }

    public Comment createComment(Post post, Long writerId, String writer, String content, Comment parentComment) {

        if (this == COMMENT) {
            return new Comment(post, writerId, writer, content, this);
        }

        return new Comment(post, writerId, writer, content, parentComment);
    }
}
