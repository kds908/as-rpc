## Practice 03
**重载**

方法重载后，相同方法名，不同参数，之前的provider服务就不再满足需求。

1. 个性 RpcRequest， 变更方法名为方法签名
2. 定义工具类方法，拼接方法签名，这里使用 方法名@参数个数_参数1_2_3... 的签名方式
3. ProviderMeta

**类型转换**
返回的类型不匹配时，会报错。

实际返回结果位于 as.rpc.core.consumer.ASInvocationHandler#invoke 中 RpcResponse
1. 创建类型转换工具类 as.rpc.core.util.TypeUtils
2. 对基本数据类型、数组、Map等类型转换