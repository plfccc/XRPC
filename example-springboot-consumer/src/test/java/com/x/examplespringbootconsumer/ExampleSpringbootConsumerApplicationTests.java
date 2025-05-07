package com.x.examplespringbootconsumer;

import com.x.example.common.model.User;
import com.x.example.common.service.UserService;
import com.x.xrpc.springboot.starter.annotation.RpcReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleSpringbootConsumerApplicationTests {

    @RpcReference
    private UserService userService;

    @Test
    public void test() {
        User user = new User();
        user.setName("张三");
        User user1 = userService.getUser(user);
        System.out.println(user1.getName());
    }

}
