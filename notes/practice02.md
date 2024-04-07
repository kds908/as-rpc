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