package com.x.example.common.service;


import com.x.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    default short getNumber(){
        return 1;
    }
}

