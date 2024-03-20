# as-rpc
手动实现RPC

---

## Practice 01
- as-rpc-core:
    
    rpc 核心组件

    as.rpc.core.api.RpcRequest: 请求参数类

    as.rpc.core.api.RpcResponse: 响应类

    as.rpc.demo.provider.ASProvider: 自定义注解 @ASProvider, 被此注解标记的类，标记为 Provider

    

- as-rpc-demo-api:
    
    demo 的一部分，对外提供接口API调用

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

**问题**

- 假设再增加个findById方法，参数类型为 Long，之前的method 不再满足要求。
    
    可将 method 改为方法签名，如 findById_Long。
- 远程调用依然有 toString、 hashCode 等方法，不必要，本地就可以解决
- 会有参数不匹配问题，需要类型转换处理

---

## Practice 02

- as-rpc-demo-consumer:

    demo 中的服务消费者

引入UserService，并添加Field注解 @ASConsumer， 

模拟实现远程调用 UserService#findById，从而得到返回值。

1. 在 RPC 核心模块 core 模块下，新建目录 as.rpc.core.consumer
2. 创建 Consumer 启动类 as.rpc.core.consumer.ConsumerBootstrap 实现 ApplicationContextAware
3. 创建 start() 方法 \
   a. 执行从 applicationContext 中获取 Bean 定义名 \
   b. 遍历定义名获取 Bean，并提取带有 @ASConsumer 注解的 Field\
      *Application 启动时，class被代理扩展，不能直接获取到原类中的注解信息，通过 getSuperclass() 方法逐级获取类，直至得到原类，并得到其中的注解Field*\
   c. 遍历注解 Field，通过类拿到类名（服务名） 从 consumer 桩 stub 中获取服务名Consumer，若不存在则创建Consumer \
      *创建 Consumer 采用代理的方式 Proxy.newProxyInstance(arg1, arg2, arg3)* \
      *参数 arg3 为 InvocationHandler 接口的实现类，该类中 invoke 实现请求调用，并根据method的返回类，转换返回结果类型并返回*\
   d. 通过反射将 consumer 装配到 Bean 中使用。
4. 创建配置类 ConsumerConfig，通过 @Bean 实例化 ConsumerBootstrap
5. 注入 ApplicationRunner Bean，并通过@Order(Integer.MIN_VALUE) 提高执行优先级，调用执行 ConsumerBootstrap#start() 方法
6. Consumer模块 的 Application 启动类中，注入 ApplicationRunner 的 Bean。启动后执行，从而模拟业务的调用与功能验证。


## Practice 03
