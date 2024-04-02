package as.rpc.core.consumer.http;

import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description for this class
 *
 * <p>
 * {@code @author: } abners.
 * <p>
 * {@code @date: } 2024/4/2 14:42
 */
public class OkHttpInvoker implements HttpInvoker {
    OkHttpClient client;
    final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    public OkHttpInvoker() {
        this.client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
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
