package org.example.quantumblog.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.UserMapper;
import org.example.quantumblog.model.Article;
import org.example.quantumblog.model.User;
import org.example.quantumblog.util.MarkdownConverter;
import org.example.quantumblog.util.Result;
import org.example.quantumblog.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.example.quantumblog.service.ArticleSerivce;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;


/**
 * @author xiaol
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class ArticleController {

    @Autowired
    private ArticleSerivce articleSerivce;

    @Autowired
    MarkdownConverter markdownConverter;

    @Autowired
    UserMapper userMapper;

    TimeUtil timeUtil=new TimeUtil();

    /**
     * @param articleMap 文章对象
     * @return Result:上传结果
     * @description: 上传文章
     * @path: /blog/publish-article
     * @method: POST
     * */
    @PostMapping("/publish-article")
    public Result publishArticle(@RequestBody Map<String,Object> articleMap){
        try{
            Article article=new Article();

            String title=(String) articleMap.get("title");
            article.setTitle(title);

            String author=(String) articleMap.get("author");
            article.setAuthor(author);

            User user=userMapper.getUserByUsername(author);
            //用户名不存在
            if(user==null){
                return new Result(Result.ERROR,"用户不存在",null);
            }
            //通过用户名获取用户id
            article.setAuthorId(user.getId());

            String tags=(String) articleMap.get("tags");
            article.setTags(tags);
            log.info("tags:"+tags);


            article.setCategory((String) articleMap.get("category"));

            article.setVisibility((boolean) articleMap.get("isPublic"));

            //写入的文件夹
            String dirPath="src/main/resources/static/articles/"+author+"/"+title+"/";
            File dir=new File(dirPath);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            String filePath=dirPath+author+"_"+title+".md";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write((String) articleMap.get("content"));
            } catch (IOException e) {
                log.error("Error writing to markdown file", e);
                return new Result(Result.ERROR, "Error writing to markdown file", null);
            }
            //如果数据库中可以查到相同标题的文章且作者相同则更新文章
            if(articleSerivce.getArticleByTitle(title)!=null
                    && articleSerivce.getArticleByTitle(title).getAuthor().equals(author)){
                articleSerivce.updateArticle(article);
            }else {
                articleSerivce.uploadArticle(article);
            }
            return new Result(Result.OK,"Upload successful",article);

        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @return Result:获取结果
     * @description: 根据id获取文章
     * @path: /blog/article/{id}
     * @method: GET
     * */
    @GetMapping("/article/{id}")
    public Result getArticleById(@PathVariable("id") int id){
        try{
            Article article= articleSerivce.getArticleById(id);
            return new Result(Result.OK,"获取文章成功",article);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param title 文章标题
     * @return Result:获取结果
     * @description: 根据标题获取文章
     * @path: /blog/article-title/{title}
     * @method: GET
     * @throw Exception GlobalException
     * */
    @GetMapping("/article-title/{title}")
    public Result getArticleByTitle(@PathVariable("title") String title){
        try{
            Article article= articleSerivce.getArticleByTitle(title);
            return new Result(Result.OK,"获取文章成功",article);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param article 文章对象
     * @return Result:更新结果
     * @description: 更新文章
     * @path: /blog/update-article
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article")
    public Result updateArticle(@RequestBody Article article){
        try{
            articleSerivce.updateArticle(article);
            return new Result(Result.OK,"更新成功",article);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @return Result:删除结果
     * @description: 删除文章
     * @path: /blog/delete-article/{id}
     * @method: DELETE
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-views/{id}")
    public Result updateArticleViews(@PathVariable("id") int id){
        try{
            articleSerivce.updateArticleViews(id);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @param flag 标志位 0表示点赞量-1 1表示点赞量+1
     * @return Result:更新结果
     * @description: 更新文章的点赞量
     * @path: /blog/update-article-likes/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-likes/{id}")
    public Result updateArticleLikes(@PathVariable("id") int id,@RequestParam int flag){
        try{
            articleSerivce.updateArticleLikes(id,flag);

            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @param flag 标志位 0表示评论量-1 1表示评论量+1
     * @return Result:更新结果
     * @description: 更新文章的评论量
     * @path: /blog/update-article-comments/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-comments/{id}")
    public Result updateArticleComments(@PathVariable("id") int id,@RequestParam int flag){
        try{
            articleSerivce.updateArticleComments(id,flag);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @param flag 标志位 0表示收藏量-1 1表示收藏量+1
     * @return Result:更新结果
     * @description: 更新文章的收藏量
     * @path: /blog/update-article-collects/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-collects/{id}")
    public Result updateArticleCollects(@PathVariable("id") int id,@RequestParam int flag){
        try{
            articleSerivce.updateArticleCollects(id,flag);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @return Result:更新结果
     * @description: 更新文章的转发量
     * @path: /blog/update-article-forwards/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-forwards/{id}")
    public Result updateArticleForwards(@PathVariable("id") int id){
        try{
            articleSerivce.updateArticleForwards(id);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param pageNum 当前页数
     * @param pageSize 每页的文章数量
     * @return ModelAndView:文章列表
     * @description: 获取文章列表
     * @path: /blog/article-list
     * @method: GET
     * @throw Exception GlobalException
     * */
    @GetMapping("/article-list")
    public ModelAndView index(
            @RequestParam(value = "pageNum", defaultValue = "1",required = false) int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10",required = false) int pageSize
    ){

        List<Article> articleList= articleSerivce.getArticleListByPage(pageNum,pageSize);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("pageInfo", pageInfo);
        return modelAndView;
    }


    /**
     * @param id 文章id
     * @return ModelAndView:文章详情
     * @description: 获取文章详情
     * @path: /blog/article-detail/{id}
     * @method: GET
     * @throw Exception GlobalException
     * */
    @GetMapping("/article-detail/{id}")
    public Map<String, Object> articleDetail(@PathVariable("id") int id){
        //返回的响应
        Map<String, Object> response = new HashMap<>();
        //获取文章
        Article article= articleSerivce.getArticleById(id);
        response.put("author", article.getAuthor());

        //获取文章创建时间
        Instant createInstant = Instant.ofEpochMilli(article.getCreateTimeStamp());
        LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(createInstant, ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = createLocalDateTime.format(dateTimeFormatter);
        response.put("createTime", createTime);

        //获取文章修改时间
        if (article.getUpdateTimeStamp() != 0) {
            Instant updateInstant = Instant.ofEpochMilli(article.getUpdateTimeStamp());
            LocalDateTime updateLocalDateTime = LocalDateTime.ofInstant(updateInstant, ZoneId.systemDefault());
            String updateTime = updateLocalDateTime.format(dateTimeFormatter);
            response.put("updateTime", updateTime);
        }

        //获取文章在数据库中的标题以及文件夹名
        String dirName=article.getTitle();
        int index = dirName.indexOf('_');
        if (index != -1) {
            dirName = dirName.substring(index + 1);
        }
        //获取md文件路径
        String path= "src/main/resources/static/articles/"
                + article.getAuthor() + "/"
                + dirName+ "/"
                + article.getAuthor()+"_"+article.getTitle() + ".md";
        //获取md文件内容
        String content= markdownConverter.readMarkdownFile(path);
        response.put("content",content);
        return response;
    }

    /**
     * @param file 上传的文件
     * @param username 用户名
     * @return Result:上传结果
     * @description: 上传本地文件
     * @path: /blog/upload
     * @method: POST
     * @throw Exception GlobalException
     * */
    @PostMapping("/upload")
    public Result uploadLocalFile(@RequestParam("file") MultipartFile file,@RequestParam String username) throws IOException, InterruptedException {
        InputStream inputStream = file.getInputStream();
        String originFileName = file.getOriginalFilename();
        String fileNameWithOutSuffix = originFileName.substring(0, originFileName.lastIndexOf("."));
        //根据flag的值判断是在数据表中创建还是更新原来的文章 flag=0表示创建，flag=1表示更新
        int flag=0;

        //检查username是否存在
        if(userMapper.getUserByUsername(username)==null){
            return new Result(Result.ERROR,"用户不存在",null);
        }
        try {
            //创建文件在服务器端的存放路径
            String saveFilePath = "src/main/resources/static/articles/" + username + "/" +fileNameWithOutSuffix + "/";
            File uploadDir = new File(saveFilePath);

            //如果这个文件夹不存在则创建
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String theFileName=username+"_"+fileNameWithOutSuffix;
            //如果文件已经存在则删除
            File saveFile = new File(saveFilePath, theFileName+".md");
            //如果文件已存在并且数据库中可以查询到同名文章或文件不存在但是数据库中可查询到文章则更新文章
            if ((saveFile.exists() && articleSerivce.getArticleByTitle(theFileName)!=null)||
                    (!saveFile.exists() && articleSerivce.getArticleByTitle(theFileName)!=null)){
                saveFile.delete();
                flag=1;
            }
            //文件名：username_title.md 这样可以避免不同用户上传同一个文件导致的冲突
            FileCopyUtils.copy(inputStream, new FileOutputStream(saveFilePath +theFileName+ ".md"));

            //创建文章对象
            if(flag==0){
                Article article = new Article();
                article.setTitle(theFileName);
                article.setAuthor(username);
                articleSerivce.uploadArticle(article);
                log.info("上传文章成功");
            }else{
                Article article= articleSerivce.getArticleByTitle(theFileName);
                article.setUpdateTimeStamp(System.currentTimeMillis());
                articleSerivce.updateArticle(article);
                log.info("更新文章成功");
            }
        } catch (IOException e) {
            throw new GlobalException("上传失败", Result.UPLOAD_FAILED);
        }
        return new Result(Result.OK, "上传成功", null);
    }

    /**
     * @param id 文章id
     * @return Result:删除结果
     * @description: 删除文章
     * @path: /blog/delete-article/{id}
     * @method: DELETE
     * @throw Exception GlobalException
     * */
    @DeleteMapping("/delete-article/{id}")
    public Result deleteArticle(@PathVariable("id") int id){
        try{
            articleSerivce.deleteArticleById(id);
            log.info("删除文章成功");
            return new Result(Result.OK,"删除成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    @GetMapping("/get-article-brief-info/{articleId}")
    public Result getArticleBriefInfo(@PathVariable("articleId") String articleId){
        try{
            int id=Integer.parseInt(articleId);
            Article article=articleSerivce.getArticleById(id);
            String title=article.getTitle();
            String author=article.getAuthor();
            String publishTime=timeUtil.timeStampToString(article.getCreateTimeStamp());
            String views=String.valueOf(article.getViews());
            String likes=String.valueOf(article.getLikes());
            String comments=String.valueOf(article.getComments());
            String collects=String.valueOf(article.getCollects());
            String shares=String.valueOf(article.getForwards());
            Map<String,String> response=new HashMap<>();
            response.put("title",title);
            response.put("author",author);
            response.put("publishTime",publishTime);
            response.put("views",views);
            response.put("likes",likes);
            response.put("comments",comments);
            response.put("collects",collects);
            response.put("shares",shares);
            return new Result(Result.OK,"获取文章简要信息成功",response);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    @PostMapping("/getArticleList")
    public Result getArticleList(@RequestBody Map<String,Object> map){
        try{
            int pageNum=(int) map.get("pageNum");
            int pageSize=(int) map.get("pageSize");
            List<Article> articleList= articleSerivce.getArticleListByPage(pageNum,pageSize);
            return new Result(Result.OK,"获取文章列表成功",articleList);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

}
