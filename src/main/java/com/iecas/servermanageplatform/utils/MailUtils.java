/**
 * @Time: 2024/8/30 16:17
 * @Author: guoxun
 * @File: MailUtil
 * @Description: 发送邮件工具类
 */

package com.iecas.servermanageplatform.utils;

import com.iecas.servermanageplatform.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class MailUtils {

    // 发件人邮箱
    private static final String SEND_EMAIL = "1785158284@qq.com";
    // 授权码
    private static final String AUTHORITY_CODE = "lejnybgpfbkdcgif";
    // 指定发邮件的主机
    private static final String HOST = "smtp.qq.com";


    /**
     * 检查邮箱格式是否正确
     * @param email
     * @return 正确：true 错误: false
     */
    public static Boolean checkEmailIsCorrect(String email){
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }


    /**
     * 发送邮件到指定邮箱
     * @param message 邮件内容
     * @param targetEmail 目标邮箱
     * @param expiredTime 过期时间, 单位minutes
     */
    public static void sendRandomCode(String message, String targetEmail, String expiredTime) throws Exception{

        if (!checkEmailIsCorrect(targetEmail)){
            throw new CommonException("邮箱格式错误");
        }

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", HOST);
        properties.put("mail.smtp.auth", true);
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SEND_EMAIL, AUTHORITY_CODE);
            }
        });
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(SEND_EMAIL));
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(targetEmail));
        mimeMessage.setSubject("【验证码】🚀 服务器统一管理平台");
        mimeMessage.setText("尊敬的用户您好, 您本次所需的验证码为: " + message + "," + expiredTime + "分钟内有效。");
        // TODO 测试环境下不真正发送邮件, 生产环境下解开下述注释
        // Transport.send(mimeMessage);
    }
}
