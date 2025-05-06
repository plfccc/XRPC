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
import com.x.rpc.RpcApplication;
import com.x.rpc.registry.LocalRegistry;
import com.x.rpc.server.HttpServer;
import com.x.rpc.server.tcp.VertxTcpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init();
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动TCP服务
        HttpServer httpServer = new VertxTcpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
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
└── example-provider/      # 提供者示例
```

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