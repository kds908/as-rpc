package as.rpc.core.consumer;

import as.rpc.core.api.RpcRequest;
import as.rpc.core.api.RpcResponse;
import as.rpc.core.meta.InstanceMeta;

/**
 * Description for this class
 *
 * <p>
 * {@code @author: } abners.
 * <p>
 * {@code @date: } 2024/4/2 14:42
 */
public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
