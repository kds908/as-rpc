package as.rpc.core.filter;

import as.rpc.core.api.Filter;
import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description for this class
 *
 * <p>
 * {@code @author: } Abner Song
 * <p>
 * {@code @date: } 2024/4/10 16:33
 */
public class CacheFilter implements Filter {

    // todo 优化策略：替换成guava cache 加容量和过期时间
    static Map<String, RpcResponse> cache = new ConcurrentHashMap<>();
    @Override
    public RpcResponse preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public RpcResponse postFilter(RpcRequest request, RpcResponse response) {
        cache.putIfAbsent(request.toString(), response);
        return response;
    }
}
