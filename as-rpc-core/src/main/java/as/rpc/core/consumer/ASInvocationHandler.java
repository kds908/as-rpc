package as.rpc.core.consumer;

import as.rpc.core.api.RpcContext;
import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.consumer.http.OkHttpInvoker;
import as.rpc.core.meta.InstanceMeta;
import as.rpc.core.util.MethodUtils;
import as.rpc.core.util.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 消费端动态代理处理类
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/19 22:00
 */
public class ASInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext context;
    List<InstanceMeta> providers;
    HttpInvoker httpInvoker = new OkHttpInvoker();

    public ASInvocationHandler(Class<?> service, RpcContext context,
                               List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.methodSign(method));
        request.setArgs(args);

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        System.out.println("loadBalancer.choose(urls) ===> " + instance);

        RpcResponse<?> response = httpInvoker.post(request, instance.toUrl());
        if (response.isStatus()) {
            // 返回结果转为java Object, 返回类型为方法的 return type
            Object data = response.getData();
            return TypeUtils.castMethodResult(method, data);
        } else {
            throw response.getEx();
        }
    }

}
