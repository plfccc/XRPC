package com.x.example.provider;


import com.x.example.common.model.User;
import com.x.example.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
    
    @Override
    public short getNumber() {
        System.out.println("调用getNumber方法");
        return 1;
    }
}
