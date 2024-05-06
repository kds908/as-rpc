package as.rpc.core.consumer;

import as.rpc.core.api.Filter;
import as.rpc.core.api.RpcContext;
import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.consumer.http.OkHttpInvoker;
import as.rpc.core.meta.InstanceMeta;
import as.rpc.core.util.MethodUtils;
import as.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

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
@Slf4j
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
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }
        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.methodSign(method));
        request.setArgs(args);

        for (Filter filter : context.getFilters()) {
            Object preResult = filter.preFilter(request);
            if (preResult != null) {
                log.debug("filter.preFilter(request) ===> {}", preResult);
                return preResult;
            }
        }

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        log.debug("loadBalancer.choose(urls) ===> " + instance);

        RpcResponse<?> response = httpInvoker.post(request, instance.toUrl());
        Object result = castReturnResult(method, response);

        // 这里拿到的可能不是最终值，需要再设计一下
        for (Filter filter : context.getFilters()) {
            Object filterResult = filter.postFilter(request, response, result);
            if (filterResult != null) {
                return filterResult;
            }
        }

        return result;
    }

    private static Object castReturnResult(Method method, RpcResponse<?> response) throws Exception {
        if (response.isStatus()) {
            // 返回结果转为java Object, 返回类型为方法的 return type
            return TypeUtils.castMethodResult(method, response.getData());
        } else {
            throw response.getEx();
        }
    }

}
