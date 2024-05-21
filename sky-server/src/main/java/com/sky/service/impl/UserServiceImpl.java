package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.WeChatConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获取到当前微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断是否为新用户，如果是新用户，自动注册
        User user = userMapper.getUserByOpenid(openid);
        if(user == null){
            user = User.builder()
                    .id(UUIDUtil.getUUID())
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //调用mapper插入新用户
            userMapper.insertUser(user);
        }

        //返回user
        return user;
    }

    /**
     * 调用微信接口服务，获取用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        HashMap<String, String> map = new HashMap<>();
        map.put(WeChatConstant.WECHAT_LOGIN_APPID_KEY,weChatProperties.getAppid());
        map.put(WeChatConstant.WECHAT_LOGIN_SECRET_KEY,weChatProperties.getSecret());
        map.put(WeChatConstant.WECHAT_LOGIN_JS_CODE_KEY, code);
        map.put(WeChatConstant.WECHAT_LOGIN_GRANT_TYPE_KEY,WeChatConstant.WECHAT_LOGIN_GRANT_TYPE_VALUE);
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString(WeChatConstant.WECHAT_LOGIN_OPENID_KEY);
    }
}
