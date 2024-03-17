package org.example.quantumblog.service;

import org.springframework.stereotype.Service;
import org.example.quantumblog.model.Comment;
import java.util.List;

/**
 * @author xiaol
 */
@Service
public interface CommentService {
    Comment addComment(String content, String articleTitle, String commentator, String parentCommentator, String author);

    List<Comment> getCommentListByPage(int pageNum, int pageSize, int articleId);
}
