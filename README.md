# as-rpc
手动实现RPC

## Practice 01
- as-rpc-core:
    
    rpc 核心组件

    as.rpc.core.api.RpcRequest: 请求参数类

    as.rpc.core.api.RpcResponse: 响应类

    as.rpc.demo.provider.ASProvider: 自定义注解 @ASProvider, 被此注解标记的类，标记为 Provider

    

- as-rpc-demo-api:
    
    demo 的一部分

- as-rpc-demo-provider:

    demo 中服务的提供者

在 as.rpc.demo.provider.AsRpcDemoProviderApplication 中添加 @RestController,使用 Http + JSON 实现序列化和通信。

**启动测试**
1. 使用 @PostConstruct 注解，初始化方法
2. 将带有 @ASProvider 自定义注解的 Bean 获取到，注册到 skeleton。 
3. 发起 Http 请求时，通过请求参数 RpcRequest 中的 service 获取到请求的服务名，从 skeleton 中取出服务Bean
4. 从请求参数RpcRequest中获取请求方法 method
5. 利用反射执行该 Bean 的方法(invoke(bean, args[]))得到返回结果
6. 将返回结果封装到 RpcResponse 返回类并返回

**代码迁移：**
1. 将 Provider 迁移至 core 下，创建 Provider 启动类 ProviderBootstrap
2. 将前面与业务和服务无关的内容迁移至 ProviderBootstrap 启动类
3. 启动类实现 org.springframework.context.ApplicationContextAware，保持applicationContext 变量名
4. 创建配置类 ProviderConfig，用于创建 ProviderBootstrap Bean
5. 在 provider demo 的 application 启动类中@Import ProviderConfig.class，即可使用 @Autowired 注入调用 ProviderBootstrap Bean
6. 启动Application 验证
7. 创建 .http 文件，测试请求
   请求方式 POST
   CONTENT-TYPE application/json
   请求参数 RpcRequest json
8. 返回结果



