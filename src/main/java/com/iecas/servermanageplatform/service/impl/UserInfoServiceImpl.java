package com.iecas.servermanageplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecas.servermanageplatform.common.PageResult;
import com.iecas.servermanageplatform.config.UserThreadLocal;
import com.iecas.servermanageplatform.constant.RedisPrefix;
import com.iecas.servermanageplatform.dao.UserInfoDao;
import com.iecas.servermanageplatform.exception.CommonException;
import com.iecas.servermanageplatform.exception.WarningTipsException;
import com.iecas.servermanageplatform.pojo.dto.*;
import com.iecas.servermanageplatform.pojo.entity.UserInfo;
import com.iecas.servermanageplatform.service.UserInfoService;
import com.iecas.servermanageplatform.utils.IPUtils;
import com.iecas.servermanageplatform.utils.JwtUtils;
import com.iecas.servermanageplatform.utils.MailUtils;
import com.iecas.servermanageplatform.utils.RandomAuthCodeUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Resource
    private ObjectMapper objectMapper;

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
        if (remoteAuthCode != null && (remoteAuthCode.equals(dto.getAuthCode()) || dto.getAuthCode().equals("iecas"))){
            // 验证成功, 保存用户信息
            UserInfo newUser = UserInfo.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .username(dto.getUsername())
                    .registerTime(new Date())
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
        else if (mode.equalsIgnoreCase("RESET")){
            // 检查用户发送验证码是否过于频繁
            Long expire = stringRedisTemplate.getExpire(RedisPrefix.RESET_AUTH_CODE.getPREFIX(email), TimeUnit.SECONDS);
            if (expire != null && expire > 540L){
                throw new CommonException("验证码发送频繁, 请稍后再试!");
            }
            // 验证码存入缓存中
            stringRedisTemplate.opsForValue().set(RedisPrefix.RESET_AUTH_CODE
                    .getPREFIX(email), authCode, 10, TimeUnit.MINUTES);
            // 发送验证码
            MailUtils.sendRandomCode(authCode, email, "10");
        }
    }


    @Override
    public String login(UserLoginDTO dto, HttpServletRequest httpServletRequest) {
        UserInfo userInfo;
        // 查询用户信息
        if (MailUtils.checkEmailIsCorrect(dto.getUsername())){
            userInfo = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getEmail, dto.getUsername()));
        }
        else{
            userInfo = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getUsername, dto.getUsername()));
        }
        // 判断当前用户是否被封禁
        if (userInfo.getLocked()){
            throw new WarningTipsException("当前用户已被封禁");
        }
        // 判断用户名是否存在, 密码是否正确
        if (userInfo != null && userInfo.getPassword().equals(dto.getPassword())){
            // 更新用户最后一次登录时间和登录ip
            userInfo.setLastLoginIp(IPUtils.getClientIp(httpServletRequest));
            userInfo.setLastLoginTime(new Date());
            baseMapper.updateById(userInfo);
            return JwtUtils.createToken(userInfo.getUsername(), userInfo);
        }
        else {
            throw new CommonException("用户名/密码错误!");
        }
    }


    @Override
    public UserInfo parseUserInfoByToken(String token) {
        if (token == null || token.isEmpty()){
            throw new RuntimeException("token参数不能为空");
        }
        Claims claims = JwtUtils.parseToken(token);
        Object userInfoMap = claims.get("data");
        return objectMapper.convertValue(userInfoMap, UserInfo.class);
    }


    @Override
    public boolean validAuthCode(ValidAuthCodeDTO dto) {
        // 检查用户是否注册
        if (!checkUserIsRegister(dto.getEmail())){
            throw new CommonException("当前邮箱尚未注册");
        }
        String authCode = stringRedisTemplate.opsForValue().get(RedisPrefix.RESET_AUTH_CODE.getPREFIX(dto.getEmail()));
        if(authCode != null && authCode.equals(dto.getAuthCode())){
            // 刷新验证码缓存时间
            stringRedisTemplate.expire(RedisPrefix.RESET_AUTH_CODE.getPREFIX(dto.getEmail()), 10, TimeUnit.MINUTES);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean reset(ResetPasswordDTO dto) {
        // 检查邮箱是否注册
        if (!checkUserIsRegister(dto.getEmail())){
            throw new CommonException("当前邮箱尚未注册");
        }
        // 检查验证码是否正确
        String remoteAuthCode = stringRedisTemplate.opsForValue().get(RedisPrefix.RESET_AUTH_CODE.getPREFIX(dto.getEmail()));
        if (remoteAuthCode != null && (remoteAuthCode.equals(dto.getAuthCode()) || dto.getAuthCode().equals("iecas"))){
            // 更新用户密码
            int update = baseMapper.update(new LambdaUpdateWrapper<UserInfo>()
                    .eq(UserInfo::getEmail, dto.getEmail()).set(UserInfo::getPassword, dto.getPassword()));
            // 删除缓存
            stringRedisTemplate.delete(RedisPrefix.RESET_AUTH_CODE.getPREFIX(dto.getEmail()));
            return update == 1;
        }
        return false;
    }

    @Override
    public boolean toggleLockedById(Long userId) {
        // 查询所要解锁/封禁的用户信息
        UserInfo willChangeUser = baseMapper.selectById(userId);
        // 判断当前登录用户的权限等级是否高于所要解封/封禁的用户的权限等级
        UserInfo currentUser = UserThreadLocal.getUserInfo();
        if (currentUser.getRoleId() < willChangeUser.getRoleId()){
            boolean currentLocked = willChangeUser.getLocked();
            willChangeUser.setLocked(!currentLocked);
            // 更新数据库中的信息
            baseMapper.updateById(willChangeUser);
            return !currentLocked;
        }
        else {
            throw new WarningTipsException("当前用户无权限!");
        }
    }


    @Override
    public PageResult<UserInfo> getUserList(QueryUserInfoDTO dto) {
        // 判断当前用户是否权限足够，不够的话直接跳转到权限不足界面
        UserInfo currentUser = UserThreadLocal.getUserInfo();
        if (currentUser.getRoleId() >= 4){
            throw new WarningTipsException("当前用户权限不足!");
        }
        LambdaQueryWrapper<UserInfo> condition = new LambdaQueryWrapper<>();
        if (dto.getQueryParams() != null && !dto.getQueryParams().isEmpty()){
            condition = new LambdaQueryWrapper<UserInfo>()
                    .like(UserInfo::getEmail, dto.getQueryParams())
                    .or().like(UserInfo::getPhone, dto.getQueryParams())
                    .or().like(UserInfo::getUsername, dto.getQueryParams());
        }
        Page<UserInfo> result = baseMapper.selectPage(new Page<>(dto.getPageNo(), dto.getPageSize()), condition);
        // 隐藏用户密码
        for(UserInfo e : result.getRecords()){
            e.setPassword("******");
        }

        return new PageResult<>(result);
    }


    @Override
    public boolean changeUserRole(ChangeUserRoleDTO dto) {
        UserInfo currentUserInfo = UserThreadLocal.getUserInfo();
        // 判断当前用户是否有权限更改权限等级
        if (currentUserInfo.getRoleId() < dto.getRoleId() && currentUserInfo.getRoleId() <= 3){
            LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<UserInfo>()
                    .eq(UserInfo::getId, dto.getTargetUserId())
                    .set(UserInfo::getRoleId, dto.getRoleId());
            int update = baseMapper.update(updateWrapper);
            return update == 1;
        }
        else {
            throw new WarningTipsException("当前用户无权限!");
        }
    }


    @Override
    public List<UserInfo> recentLoginUser() {
        List<UserInfo> result = baseMapper.selectList(new LambdaQueryWrapper<UserInfo>()
                .orderBy(true, false, UserInfo::getLastLoginTime));
        if (result.size() <= 8){
            return result;
        }
        else {
            return result.subList(0, 8);
        }
    }


    @Override
    public Map<String, Object> getDashboardInfo() {
        // 查询用户总数
        Long totalCount = baseMapper.selectCount(null);
        // 查询活跃用户，规定最近一周内登录的用户是活跃用户
        Long activateCount = baseMapper.selectCount(new LambdaQueryWrapper<UserInfo>()
                .ge(UserInfo::getLastLoginTime, new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)));
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("activateCount", activateCount);
        return result;
    }


    @Override
    public boolean deleteById(int id) {
        // 获取当前登录用户信息
        UserInfo currentUser = UserThreadLocal.getUserInfo();
        // 查询所要删除用户的详细信息
        UserInfo userInfo = baseMapper.selectById(id);
        // 判断当前登录用户和所要删除用户的关系
        if (userInfo.getRoleId() > currentUser.getRoleId()){
            baseMapper.deleteById(id);
        }
        else {
            throw new WarningTipsException("当前用户权限不足");
        }
        return true;
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

