package org.example.quantumblog.service;

import org.example.quantumblog.entity.auth.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * 发送验证码服务
 */
@Service
public class SendCodeService {
    @Autowired
    private MailSender mailSender;

    @Autowired
    private RedisTemplate<String, VerificationCode> redisTemplate;

    private VerificationCode verificationCode = new VerificationCode();

    //生成验证码
    public int generateCode() {
        //生成6位随机数
        return (int) ((Math.random() * 9 + 1) * 100000);

        //验证码随机生成后放入redis缓存
    }

    //发送验证码
    public void sendCode(int code,String receiver) {
        SimpleMailMessage message = new SimpleMailMessage();
        String codeStr = String.valueOf(code);
        message.setFrom("localhost@admin.com");
        message.setTo(receiver);
        message.setSubject("验证码");
        message.setText("尊敬的xxx,您好:\n"
                + "\n本次请求的邮件验证码为:" + codeStr + ",本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");

        //发送邮件
        mailSender.send(message);

        //将验证码存入redis缓存，设置过期时间为5分钟
        verificationCode.setCode(codeStr);
        verificationCode.setExpireTime(System.currentTimeMillis() + 300000);
        verificationCode.setReceiver(receiver);

        ValueOperations<String, VerificationCode> operations = redisTemplate.opsForValue();
        operations.set(receiver, verificationCode, 300000);

    }

    //验证验证码
    public boolean verifyCode(int code,String receiver) {
        return false;
    }
}
