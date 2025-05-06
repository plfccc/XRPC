package com.x.example.consumer;

import com.x.example.common.model.User;
import com.x.example.common.service.UserService;
import com.x.rpc.proxy.ServiceProxyFactory;

/**
 * @author lingpfeng
 * @date 2025/04/26 19:23:37
 * @description TODO
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("lingpfeng");
        // 调用服务
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("新用户名：" + newUser.getName());
        }else {
            System.out.println("获取用户失败");
        }
        long number = userService.getNumber();
        System.out.println("number:"+number);
    }
}
