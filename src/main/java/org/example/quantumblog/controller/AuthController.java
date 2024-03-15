package org.example.quantumblog.controller;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.model.AuthRequest;
import org.example.quantumblog.model.User;
import org.example.quantumblog.service.EmailService;
import org.example.quantumblog.util.Generator;
import org.example.quantumblog.util.JwtUtils;
import org.example.quantumblog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import org.example.quantumblog.service.auth.AuthService;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.example.quantumblog.util.Result.*;

/**
 * @author xiaol
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private  Producer captchaMathProducer;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JavaMailSender emailSender;

    /**
    * @return byte[]:captcha
    * @throw IOException
    * @description:生成验证码
    * @path:/auth/captcha
    * @method:GET
    * */
    @GetMapping("/captcha")
    @ResponseBody
    public byte[] captcha() throws IOException {
        // 随机选择验证码类型
        DefaultKaptcha producer = (DefaultKaptcha) (new Random().nextBoolean() ? captchaProducer : captchaMathProducer);

        // 生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        // 将验证码存储在 Redis 中，并设置过期时间为 1 分钟
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        ops.set("captcha", text, 1, TimeUnit.MINUTES);

        // 将验证码图片转换为 Base64 编码的字符串
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * @param authRequest 用户名、密码、验证码
     * @return Result 登录结果
     * @description: 登录功能
     * @path: /auth/login
     * @method: POST
    * */
    @PostMapping("/login")
    public Result login(@RequestBody AuthRequest authRequest){
        try{
            Map<String,Object> response;
            response=authService.login(authRequest);
            return new Result(OK,"登录成功",response);
        }catch (Exception e){
            return new Result(UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param cookie 是否包含token，包含则重定向到首页，否则返回登录页面
     * @return ModelAndView 登录页面
     * @description: 登录页面
     * @path: /auth/login
     * @method: GET
     */

    @GetMapping("/login")
    public ModelAndView login(@RequestHeader(value="cookie",required = false) String cookie){
        return new ModelAndView("/login");
    }

    /**
    * @param authRequest 用户名、密码、邮箱、验证码
    * @return Result 注册结果
    * @description: 邮箱注册功能
    * @path: /auth/registerByEmail
    * @method: POST
    * */
    @PostMapping("/registerByEmail")
    public Result register(@RequestBody AuthRequest authRequest){
        try{
            Map<String,Object> response;
            response=authService.registerByEmail(authRequest);
            return new Result(OK,"注册成功",response);
        }catch(Exception e){
            return new Result(REGISTER_FAILED,e.getMessage(),"尝试次数："+authRequest.getFailedAttempts());
        }
    }

    /**
     * @return ModelAndView 注册页面
     * @description: 注册页面
     * @path: /auth/register
     * @method: GET
     * */
    @GetMapping("/register")
    public ModelAndView register(){
        return new ModelAndView("register");
    }

    /**
     * @param request 邮箱、旧密码、新密码、验证码
     * @return Result 修改密码结果
     * @description: 修改密码
     * @path: /auth/changePassword
     * @method: PUT
     * */
    @PutMapping("/changePassword")
    public Result changePassword(@RequestBody Map<String,String> request){
        try{
            authService.changePassword(request.get("username"),request.get("oldPassword"),request.get("newPassword"));
            return new Result(OK,"修改密码成功",null);
        }catch (Exception e){
            return new Result(UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、新邮箱、验证码
     * @return Result 修改邮箱结果
     * @description: 修改邮箱
     * @path: /auth/changeEmail
     * @method: PUT
     * */
    @PutMapping("/changeEmail")
    public Result changeEmail(@RequestBody Map<String,String> request){
        try{
            authService.changeEmail(request.get("username"),request.get("password"),request.get("newEmail"));
            return new Result(OK,"修改邮箱成功",null);
        }catch (Exception e){
            return new Result(UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、新手机号、验证码
     * @return Result 修改手机号结果
     * @description: 修改手机号
     * @path: /auth/changePhone
     * @method: PUT
     * */
    @PutMapping("/changePhone")
    public Result changePhone(@RequestBody Map<String,String> request){
        try{
            authService.changePhone(request.get("username"),request.get("password"),request.get("newPhone"));
            return new Result(OK,"修改手机号成功",null);
        }catch (Exception e){
            return new Result(UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 发送的邮箱、主题、验证码
     * @return Result 发送邮件结果
     * @description: 发送邮件
     * @path: /auth/sendEmail
     * @method: POST
     * */
    @PostMapping("/sendEmail")
    public Result sendEmail(@RequestBody Map<String,String> request){
        try{
            String to=request.get("email");
            String code= Generator.generateRandomCode();
            String subject=request.get("subject");
            EmailService emailService=new EmailService(emailSender);
            stringRedisTemplate.opsForValue().set(to,code,3, TimeUnit.MINUTES);
            emailService.sendEmail(to,subject,code);
            return new Result(OK,"发送邮件成功",null);
        }catch (Exception e){
            return new Result(UNAUTHORIZED,e.getMessage(),null);
        }
    }

    @PostMapping("/validateToken")
    public Result validateToken(@RequestBody Map<String,String> request){
        try{
            String token=request.get("token");
            String username=JwtUtils.parseToken(token).get("username").toString();
            return new Result(OK,"验证成功",username);
        }catch (Exception e){
            return new Result(UNAUTHORIZED,e.getMessage(),null);
        }
    }

    @PostMapping("/loginByCode")
    public Result loginByCode(@RequestBody AuthRequest authRequest){
        try{
            String account=authRequest.getAccount().get("account");
            // 从Redis中获取存储的验证码
            String storedCode = stringRedisTemplate.opsForValue().get(account);

            // 检查验证码是否存在
            if (storedCode == null) {
                return new Result(UNAUTHORIZED, "验证码不存在或已过期", null);
            }

            // 检查验证码是否匹配
            if (!storedCode.equals(authRequest.getAccount().get("code"))) {
                return new Result(UNAUTHORIZED, "验证码错误", null);
            }

            // 验证码匹配，用户通过身份验证
            String token = JwtUtils.generateToken(authRequest.getAccount().get("account"), authRequest.getAccount().get("code"));

            // 创建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);

            // 返回成功结果
            return new Result(OK, "登录成功", responseData);
        } catch (Exception e) {
            return new Result(UNAUTHORIZED, e.getMessage(), null);
        }
    }
}
