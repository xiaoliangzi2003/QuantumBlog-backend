package org.example.quantumblog.service;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.cond.ArticleCond;
import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.ArticleMapper;
import org.example.quantumblog.mapper.CommentMapper;
import org.example.quantumblog.model.Article;
import org.example.quantumblog.model.Comment;
import org.example.quantumblog.util.FileUtils;
import org.example.quantumblog.util.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.example.quantumblog.util.Result.*;

/**
 * @author xiaol
 */
@Service
@Slf4j
public class ArticleSerivceImpl implements ArticleSerivce {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private Generator generator;

    @Transactional
    @Override
    public void uploadArticle(Article article) {
        //判断文章的属性
        if(article==null){
            throw new GlobalException("请求为空",NULL_REQUEST);
        }
        if(StringUtils.isBlank(article.getTitle())){
            throw new GlobalException("标题为空",NULL_TITLE);
        }
        if(article.getTitle().length()>50){
            throw new GlobalException("标题过长",TOO_LONG_TITLE);
        }
        if(StringUtils.isBlank(article.getAuthor())){
            throw new GlobalException("请填写作者名",NULL_AUTHOR);
        }
        article.setTop(false);
        article.setViews(0);
        article.setLikes(0);
        article.setComments(0);
        article.setCollects(0);
        article.setForwards(0);
        Date now=new Date();
        long nowStamp=now.getTime();
        article.setCreateTimeStamp(nowStamp);
        article.setUpdateTimeStamp(nowStamp);
        long id=generator.generateUniqueNumber(article.getAuthor(),nowStamp);
        article.setId(id);
        article.setStatus("publish");
        //如果没有标签，就默认为"未分类"
        if(StringUtils.isBlank(article.getTags())){
            article.setTags("未分类");
        }
        //如果没有类型，就默认为"原创"
        if(StringUtils.isBlank(article.getType())){
            article.setType("原创");
        }
        article.setUrl("/"+article.getId());
        articleMapper.addArticle(article);
    }

    @Override
    public Article getArticleById(Integer id) {
        if(id==null){
            throw new GlobalException("请求为空",NULL_REQUEST);
        }
        return articleMapper.getArticleById(id);
    }

    @Override
    public Article getArticleByTitle(String title) {
        if(StringUtils.isBlank(title)){
//            throw new GlobalException("标题为空",NULL_TITLE);
            return null;
        }
        Article article=articleMapper.getArticleByTitle(title);
        if(article==null){
//            throw new GlobalException("文章不存在",NULL_ARTICLE);
            return null;
        }else {
            return article;
        }
    }

    @Override
    @Transactional
    public void updateArticle(Article article) {
        if(article==null){
            throw new GlobalException("请求为空",NULL_REQUEST);
        }
        if(StringUtils.isBlank(article.getTitle())){
            throw new GlobalException("标题为空",NULL_TITLE);
        }
        if(article.getTitle().length()>50){
            throw new GlobalException("标题过长",TOO_LONG_TITLE);
        }
        if(StringUtils.isBlank(article.getAuthor())){
            throw new GlobalException("请填写作者名",NULL_AUTHOR);
        }
        article.setUpdateTimeStamp(new Date().getTime());
        articleMapper.updateArticle(article);
    }

    @Override
    public List<Article> getArticleListByPage(int pageNum, int pageSize) {
        if(pageNum<1){
            throw new GlobalException("页数错误",WRONG_PAGE_NUM);
        }
        if(pageSize<1){
            throw new GlobalException("每页数量错误",WRONG_PAGE_SIZE);
        }
        PageHelper.startPage(pageNum,pageSize);
        return articleMapper.getArticleList();
    }

    @Override
    @Transactional
    //id：文章id
    public void deleteArticleById(Integer id) {
        if(id==null){
            throw new GlobalException("请求为空",NULL_REQUEST);
        }else if(articleMapper.getArticleById(id)==null){
            throw new GlobalException("文章不存在",NULL_ARTICLE);
        }
        //删除本地文件夹
        String username=articleMapper.getArticleById(id).getAuthor();
        String dirName=articleMapper.getArticleById(id).getTitle();
        int index = dirName.indexOf('_');
        if (index != -1) {
            dirName = dirName.substring(index + 1);
        }
        //删除该文件夹下的所有文件
        String dirPath="src/main/resources/static/articles/"+username+"/"+dirName;
        File dir=new File(dirPath);
        FileUtils.deleteDirectory(dir);

        //删除文章
        articleMapper.deleteArticle(id);
        //删除文章的评论
        List<Comment> comments= commentMapper.getCommentListByArticleId(id);
        for(Comment comment:comments){
            commentMapper.deleteCommentById(comment.getId());
        }
    }

    @Override
    public void updateArticleViews(Integer id) {
        if(id==null){
            throw new GlobalException("请求为空",NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        article.setViews(article.getViews()+1);
        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleLikes(Integer id,int flag) {
        if(id==null){
            throw new GlobalException("请求为空",NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        if(flag==0){
            article.setLikes(article.getLikes()+1);
        }else{
            article.setLikes(article.getLikes()-1);
        }

        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleComments(Integer id,int flag) {
        if (id == null) {
            throw new GlobalException("请求为空", NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        if(flag==0) {
            article.setComments(article.getComments() + 1);
        }else{
            article.setComments(article.getComments() - 1);
        }
        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleCollects(Integer id,int flag) {
        if (id == null) {
            throw new GlobalException("请求为空", NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        if(flag==0) {
            article.setCollects(article.getCollects() + 1);
        }else{
            article.setCollects(article.getCollects() - 1);
        }
        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleForwards(Integer id) {
        if (id == null) {
            throw new GlobalException("请求为空", NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        article.setForwards(article.getForwards() + 1);
        articleMapper.updateArticle(article);
    }
}
