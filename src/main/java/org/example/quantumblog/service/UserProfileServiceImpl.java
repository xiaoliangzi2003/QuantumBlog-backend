package org.example.quantumblog.service;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.mapper.UserProfileMapper;
import org.example.quantumblog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author xiaol
 */
@Service
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    UserProfileMapper userProfileMapper;

    @Override
    public void changeAvatar(String username, MultipartFile avatar) {
        try{
            String dirPath="src/main/resources/static/image/avatar/";
            File dir= new File(dirPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            String filePath=dirPath+username+".jpg";
            File avatarFile = new File(filePath);

            if(avatarFile.exists()){
                avatarFile.delete();
            }
            /*
            * 图片数据是二进制，需要使用FileOutputStream将其写入文件
            * 而BufferedWriter是字符流，不能直接写入二进制数据,会导致图片损坏
            * */
            FileOutputStream fos = new FileOutputStream(avatarFile);
            fos.write(avatar.getBytes());
            fos.close();

            //将文件路径存入数据库
            String url="/image/avatar/"+username+".jpg";
            userProfileMapper.changeAvatar(username,url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getAvatar(String username) {
        //读取图片数据
        String filePath="src/main/resources/static/image/avatar/"+username+".jpg";
        File file = new File(filePath);
        if(file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[fis.available()];
                fis.read(data);
                fis.close();
                return data;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
