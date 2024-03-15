package org.example.quantumblog.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.model.Article;
import org.example.quantumblog.service.ArticleSerivce;
import org.example.quantumblog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.example.quantumblog.util.Result.*;

/**
 * @author xiaol
 * @description: 首页控制器
 */
@RestController
@Slf4j
public class IndexController {
    @Autowired
    ArticleSerivce articleSerivce;

    /**
     * @return ModelAndView:pageInfo 文章分页信息
     * @description: 获取首页文章列表
     * @path: /index
     * @method:GET
     * */
    @GetMapping("/index")
    public ModelAndView index(){
        List<Article> articleList= articleSerivce.getArticleListByPage(1,10);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("pageInfo", pageInfo);
        return modelAndView;
    }

    /**
     * @return ModelAndView:edit 文章编辑界面
     * @description: 跳转到文章编辑界面
     * @path: /edit
     * @method:GET
     * */
    @GetMapping("/edit")
    public ModelAndView edit(){
        return new ModelAndView("edit");
    }

}
