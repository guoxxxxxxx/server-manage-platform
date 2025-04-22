package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecas.servermanageplatform.constant.RedisPrefix;
import com.iecas.servermanageplatform.dao.UserInfoDao;
import com.iecas.servermanageplatform.exception.CommonException;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.dto.UserRegisterDTO;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.service.UserInfoService;
import com.iecas.servermanageplatform.utils.MailUtils;
import com.iecas.servermanageplatform.utils.RandomAuthCodeUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * (UserInfo)表服务实现类
 *
 * @author guox
 * @since 2025-04-17 16:21:27
 */



@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoDao, UserInfo> implements UserInfoService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void register(UserRegisterDTO dto) {
        // 检查用户名是否合法, 用户名username禁止使用email形式
        if (MailUtils.checkEmailIsCorrect(dto.getUsername())){
            throw new CommonException("禁止使用Email作为用户名！");
        }
        // 检查当前用户名是否已经被使用
        if (checkUserIsRegister(dto.getUsername())){
            throw new WarningTipsException("当前用户名已被使用");
        }
        // 检查当前邮箱是否已经被注册
        if (checkUserIsRegister(dto.getEmail())){
            throw new WarningTipsException("当前邮箱已被注册");
        }
        // 检查验证码是否正确
        String remoteAuthCode = stringRedisTemplate.opsForValue().get(RedisPrefix.REGISTER_AUTH_CODE
                .getPREFIX(dto.getEmail()));
        if (remoteAuthCode != null && remoteAuthCode.equals(dto.getAuthCode())){
            // 验证成功, 保存用户信息
            UserInfo newUser = UserInfo.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .username(dto.getUsername())
                    .build();
            if (baseMapper.insert(newUser) == 0){
                throw new CommonException("注册失败, 请联系系统管理员!");
            }
            else {
                // 注册成功删除缓存中的验证码
                stringRedisTemplate.delete(RedisPrefix.REGISTER_AUTH_CODE.getPREFIX(dto.getUsername()));
            }
        }
        else {
            throw new CommonException("验证码错误！");
        }
    }


    @Override
    public void sendAuthCode(String email, String mode) throws Exception {
        // 生成随机的验证码
        String authCode = RandomAuthCodeUtils.getRandomAuthCode();
        if (mode.equalsIgnoreCase("REGISTER")){
            // 检查用户发送验证码是否过于频繁
            Long expire = stringRedisTemplate.getExpire(RedisPrefix.REGISTER_AUTH_CODE.getPREFIX(email), TimeUnit.SECONDS);
            if (expire != null && expire > 540L){
                throw new CommonException("验证码发送频繁, 请稍后再试!");
            }
            // 验证码存入缓存中
            stringRedisTemplate.opsForValue().set(RedisPrefix.REGISTER_AUTH_CODE
                    .getPREFIX(email), authCode, 10, TimeUnit.MINUTES);
            // 发送验证码
            MailUtils.sendRandomCode(authCode, email, "10");
        }
    }


    /**
     * 根据用户名或用户邮箱检查用户是否已经注册
     * @param usernameOrEmail 用户名或邮箱
     * @return true: 用户已经注册 false: 用户未注册
     */
    private boolean checkUserIsRegister(String usernameOrEmail){
        // 检查当前提供信息是否为邮箱
        Boolean isEmail = MailUtils.checkEmailIsCorrect(usernameOrEmail);
        if (isEmail){
            // 根据邮箱查询当前用户是否已经注册
            List<UserInfo> userInfos = baseMapper.selectList(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getEmail, usernameOrEmail));
            return !userInfos.isEmpty();
        }
        else {
            // 根据用户名查询用户是否已经注册
            List<UserInfo> userInfos = baseMapper.selectList(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getUsername, usernameOrEmail));
            return !userInfos.isEmpty();
        }
    }
}

