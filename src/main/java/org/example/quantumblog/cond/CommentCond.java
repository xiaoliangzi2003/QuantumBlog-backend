package org.example.quantumblog.cond;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xiaol
 * 评论查询条件
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCond {
    //开始时间
    private Integer startTime;
    //结束时间
    private Integer endTime;
    //父评论Id,0表示是一级评论
    private Integer parentId;
    //状态
    private String status;
}
