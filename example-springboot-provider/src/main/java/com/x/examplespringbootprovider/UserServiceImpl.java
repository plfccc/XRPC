package com.x.examplespringbootprovider;

import com.x.example.common.model.User;
import com.x.example.common.service.UserService;
import com.x.xrpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author lingpfeng
 * @date 2025/05/07 13:50:01
 * @description
 */
@Service
@RpcService(serviceVersion = "version1")
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("接收到信息：" + user.getName());
        return user;
    }

    @Override
    public short getNumber() {
        return UserService.super.getNumber();
    }
}
