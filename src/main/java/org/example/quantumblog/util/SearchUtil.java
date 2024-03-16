package org.example.quantumblog.util;

import org.example.quantumblog.model.Article;

import java.util.List;

/**
 * @author xiaol
 * @description: 搜索工具类
 * */
public class SearchUtil {


    /**
     * @description:  根据浏览量、点赞量、评论量、收藏量、转发量计算文章的综合得分
     * */
    public double calculateScore(
            int views,
            int collections,
            int comments,
            int shares,
            int likes
    ){
        //权重参数设置
        double viewsWeight = 0.1;
        double likesWeight = 0.2;
        double collectionsWeight = 0.2;
        double commentsWeight = 0.2;
        double sharesWeight = 0.3;

        //计算得分
        double score = views*viewsWeight
                +likes*likesWeight
                +collections*collectionsWeight
                +comments*commentsWeight
                +shares*sharesWeight;

        double e = 2.71;

        double finalScore=1/(Math.pow(e,-score/1000)+1)*5;
        //加入随机扰动

        return finalScore;
    }

}
