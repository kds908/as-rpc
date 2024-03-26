package as.rpc.core.consumer;

import as.rpc.core.api.*;
import as.rpc.core.util.MethodUtils;
import as.rpc.core.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description for this class
 *
 * <p>
 * {@code @author:} Abner Song
 * <p>
 * {@code @date:} 2024/3/19 22:00
 */
public class ASInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext context;
    List<String> providers;

    final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    public ASInvocationHandler(Class<?> service, RpcContext context,
                               List<String> providers) {
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

        List<String> urls = context.getRouter().route(this.providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        System.out.println("loadBalancer.choose(urls) ===> " + url);
        RpcResponse response = post(request, url);
        if (response.isStatus()) {
            // 返回结果转为java Object, 返回类型为方法的 return type
//            return JSONObject.toJavaObject((JSONObject) response.getData(), method.getReturnType());
            Object data = response.getData();
            if (data instanceof JSONObject jsonResult) {
                return jsonResult.toJavaObject(method.getReturnType());
            } else if (data instanceof JSONArray jsonArray) {
                Object[] array = jsonArray.toArray();
                Class<?> componentType = method.getReturnType().getComponentType();
                Object result = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    Array.set(result, i, array[i]);
                }
                return result;
            } else {
                return TypeUtils.cast(data, method.getReturnType());
            }
        } else {
            throw response.getEx();
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest request, String url) {
        String reqJson = JSON.toJSONString(request);
        Request okRequest = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();
        try {
            String respJson = Objects.requireNonNull(client.newCall(okRequest).execute().body()).string();
            return JSON.parseObject(respJson, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
