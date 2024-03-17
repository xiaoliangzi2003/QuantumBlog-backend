package org.example.quantumblog.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.service.UserProfileService;
import org.example.quantumblog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author xiaol
 */
@RestController
@RequestMapping("/user-profile")
@Slf4j
public class UserProfileController {
    @Autowired
    UserProfileService userProfileService;

    /**
     * @description: 用户更改头像
     * @param: avatar 用户上传的头像
     * @return: result {code:200,message:"success",data:{avatar:url}}
     * */
    @PostMapping("/change-avatar")
    public Result changeAvatar(MultipartFile avatar,String username){
        try{
            userProfileService.changeAvatar(username,avatar);
            return new Result(Result.OK,"success",avatar.getOriginalFilename());
        }catch (Exception e){
            log.error(e.getMessage());
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @description: 获取用户头像
     * @param: username 用户名
     * */
    @GetMapping("/get-avatar/{username}")
    @ResponseBody
    public byte[] getAvatar(@PathVariable String username){
        try{
            return userProfileService.getAvatar(username);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
