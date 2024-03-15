package org.example.quantumblog.service;
import org.example.quantumblog.cond.ArticleCond;
import org.example.quantumblog.model.Article;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author xiaol
 */
@Service
public interface ArticleSerivce {
    //上传文章
    void uploadArticle(Article article);

    //根据Id获取文章
    Article getArticleById(Integer id);

    //根据标题获取文章
    Article getArticleByTitle(String title);

    //更新文章
    void updateArticle(Article article);

    //根据条件获取分页文章列表
    List<Article> getArticleListByPage(int pageNum, int pageSize);

    //根据Id删除文章
    void deleteArticleById(Integer id);

    //更新文章的浏览量
    void updateArticleViews(Integer id);

    //更新文章的点赞量
    void updateArticleLikes(Integer id,int flag);

    //更新文章的评论量
    void updateArticleComments(Integer id,int flag);

    //更新文章的收藏量
    void updateArticleCollects(Integer id,int flag);

    //更新文章的转发量
    void updateArticleForwards(Integer id);


}
