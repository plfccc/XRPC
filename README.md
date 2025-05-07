# X-RPC 框架

## 项目介绍

X-RPC 是一个基于 Java 开发的轻量级 RPC（远程过程调用）框架，提供了简单易用的分布式服务调用解决方案。框架采用 Vert.x 作为底层通信组件，支持多种序列化方式、服务注册与发现、负载均衡和容错机制，帮助开发者快速构建高性能的分布式系统。

## 架构设计

### 整体架构

X-RPC 框架主要由以下几个核心模块组成：

- **通信模块**：基于 Vert.x 实现的 HTTP 和 TCP 服务器，负责处理网络通信
- **序列化模块**：支持多种序列化方式（JDK、JSON、Hessian、Kryo）
- **注册中心**：支持本地注册、ZooKeeper 和 Etcd 等多种注册中心
- **负载均衡**：提供随机、轮询和一致性哈希等负载均衡策略
- **容错机制**：包括重试策略和容错策略
- **代理模块**：基于动态代理实现透明的远程服务调用

### 调用流程

1. **服务提供者**：初始化 RPC 框架，注册服务到注册中心，启动服务器监听请求
2. **服务消费者**：通过代理工厂获取服务代理对象，发起远程调用
3. **请求处理**：序列化请求数据，通过网络发送到服务提供者
4. **服务执行**：服务提供者接收请求，反序列化后调用本地服务，将结果返回

## 核心功能

### 1. 多种通信协议支持

- 支持 HTTP 和 TCP 协议
- 基于 Vert.x 实现高性能的异步非阻塞通信

### 2. 灵活的序列化机制

- JDK 原生序列化
- JSON 序列化
- Hessian 序列化
- Kryo 序列化

### 3. 服务注册与发现

- 本地注册中心
- ZooKeeper 注册中心
- Etcd 注册中心
- 服务元数据管理

### 4. 负载均衡

- 随机负载均衡
- 轮询负载均衡
- 一致性哈希负载均衡

### 5. 容错机制

- 重试策略：固定间隔重试、无重试
- 容错策略：快速失败、失败安全、故障转移、失败恢复

### 6. SPI 扩展机制

- 基于 Java SPI 机制实现框架的可扩展性
- 支持自定义序列化器、负载均衡器、注册中心等组件

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.x</groupId>
    <artifactId>x-rpc-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 定义服务接口

在公共模块中定义服务接口：

```java
package com.x.example.common.service;

import com.x.example.common.model.User;

public interface UserService {
    /**
     * 获取用户
     */
    User getUser(User user);
    
    default short getNumber(){
        return 1;
    }
}
```

### 3. 实现服务接口

在服务提供者模块中实现接口：

```java
package com.x.example.provider;

import com.x.example.common.model.User;
import com.x.example.common.service.UserService;

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
```

### 4. 启动服务提供者

```java
package com.x.example.provider;

import com.x.example.common.service.UserService;
import com.x.rpc.model.ServiceRegisterInfo;
import com.x.rpc.bootstrap.ProviderBootstrap;

import java.util.ArrayList;
import java.util.List;

public class EasyProviderExample {
    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> userServiceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(userServiceRegisterInfo);
        // 初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
```

### 5. 服务消费者调用

```java
package com.x.example.consumer;

import com.x.example.common.model.User;
import com.x.example.common.service.UserService;
import com.x.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        // 获取服务代理对象
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        // 创建请求参数
        User user = new User();
        user.setName("lingpfeng");
        // 调用远程服务
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("新用户名：" + newUser.getName());
        } else {
            System.out.println("获取用户失败");
        }
        // 调用另一个方法
        long number = userService.getNumber();
        System.out.println("number:" + number);
    }
}
```

## 配置选项

框架支持通过 `rpc.yaml` 配置文件进行配置，默认配置如下：

```yaml
# RPC 框架配置
rpc:
  # 服务器主机名
  serverHost: localhost
  # 服务器端口号
  serverPort: 8080
  # 注册中心配置
  registry:
    # 注册中心类型（local, zookeeper, etcd）
    registry: local
    # 注册中心地址
    address: localhost:2181
  # 序列化器类型（JDK, JSON, Hessian, Kryo）
  serializer: Hessian
  # 负载均衡器类型（random, roundRobin, consistentHash）
  loadBalancer: random
  # 容错策略（failFast, failSafe, failOver, failBack）
  tolerantStrategy: failFast
  # 重试策略（no, fixedInterval）
  retryStrategy: fixedInterval
  # 重试次数
  retryTimes: 3
  # 重试间隔（毫秒）
  retryInterval: 1000
```

## 扩展机制

X-RPC 框架基于 SPI 机制提供了良好的扩展性，您可以通过以下步骤自定义组件：

1. 实现相应的接口（如 `Serializer`、`LoadBalancer`、`Registry` 等）
2. 在 `META-INF/services/` 目录下创建接口全限定名的文件
3. 在文件中添加实现类的全限定名

## 项目结构

```
my-rpc/
├── x-rpc-core/            # 核心实现模块
├── yu-rpc-easy/           # 简化版实现
├── example-common/        # 示例公共模块
├── example-consumer/      # 消费者示例
├── example-provider/      # 提供者示例
├── example-springboot-consumer/  # Spring Boot消费者示例
└── example-springboot-provider/  # Spring Boot提供者示例
```

## Spring Boot集成

X-RPC框架提供了与Spring Boot的无缝集成，通过注解方式简化RPC服务的开发和调用。

### 核心注解

框架提供了三个核心注解用于Spring Boot集成：

1. **@EnableRpc**：启用RPC功能的注解，添加在Spring Boot应用的启动类上
   - `needServer`：是否需要启动服务器，默认为true

2. **@RpcService**：标识服务提供者的注解，添加在服务实现类上
   - `interfaceClass`：服务接口类
   - `serviceVersion`：服务版本，默认为"1.0"

3. **@RpcReference**：服务消费者注解，用于注入远程服务，添加在需要注入服务的字段上
   - `interfaceClass`：服务接口类
   - `serviceVersion`：服务版本，默认为"1.0"
   - `loadBalancer`：负载均衡器类型，默认为轮询
   - `retryStrategy`：重试策略，默认为不重试
   - `tolerantStrategy`：容错策略，默认为快速失败
   - `mock`：是否启用模拟调用，默认为false

### 集成原理

Spring Boot集成基于以下原理实现：

1. 通过`@EnableRpc`注解导入RPC相关的配置类
2. 使用Spring的Bean生命周期钩子，在应用启动时自动注册和发现服务
3. 利用Spring的依赖注入机制，自动为标记了`@RpcReference`的字段注入代理对象
4. 自动扫描并注册标记了`@RpcService`的服务实现类

### 服务提供者示例

```java
// 1. 在启动类上添加@EnableRpc注解
@SpringBootApplication
@EnableRpc
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}

// 2. 在服务实现类上添加@RpcService注解
@RpcService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
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
```

### 服务消费者示例

```java
// 1. 在启动类上添加@EnableRpc注解
@SpringBootApplication
@EnableRpc(needServer = false) // 消费者一般不需要启动服务器
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}

// 2. 在需要使用远程服务的类中注入服务
@RestController
public class UserController {
    // 使用@RpcReference注解注入远程服务
    @RpcReference(interfaceClass = UserService.class)
    private UserService userService;
    
    @GetMapping("/user")
    public User getUser(String name) {
        User user = new User();
        user.setName(name);
        // 调用远程服务
        return userService.getUser(user);
    }
}
```

### 配置选项

在Spring Boot应用中，可以通过`application.yml`或`application.properties`配置RPC相关参数：

```yaml
# RPC配置
rpc:
  # 服务器主机名
  serverHost: localhost
  # 服务器端口号
  serverPort: 8080
  # 注册中心配置
  registry:
    # 注册中心类型（local, zookeeper, etcd）
    registry: zookeeper
    # 注册中心地址
    address: localhost:2181
  # 序列化器类型
  serializer: Hessian
  # 负载均衡器类型
  loadBalancer: roundRobin
  # 容错策略
  tolerantStrategy: failFast
  # 重试策略
  retryStrategy: fixedInterval
  # 重试次数
  retryTimes: 3
  # 重试间隔（毫秒）
  retryInterval: 1000
```

## 最近更新

- 添加了Spring Boot集成的注解支持，包括`@EnableRpc`、`@RpcService`和`@RpcReference`
- 修复了Registry SPI配置路径错误的问题，将`com.x.rpc.serializer.Registry`更正为`com.x.rpc.registry.Registry`
- 更新了服务提供者示例代码，使用`ProviderBootstrap`进行服务初始化和注册
- 添加了Spring Boot集成示例模块

## 性能优化

- 使用 Vert.x 实现高性能的异步非阻塞通信
- 支持多种高效序列化方式（如 Kryo、Hessian）
- 服务注册信息缓存机制
- 负载均衡策略优化

## 贡献指南

欢迎贡献代码或提出建议，请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件

## 联系方式

- 作者：lingpfeng
- 邮箱：your.email@example.com
- GitHub：[https://github.com/yourusername/my-rpc](https://github.com/yourusername/my-rpc)