package as.rpc.core.api;

/**
 * @author abners.
 * @description 过滤器
 * @date 2024/3/26 11:10
 */
public interface Filter {
    RpcResponse preFilter(RpcRequest request);

    RpcResponse postFilter(RpcRequest request, RpcResponse response);

    Filter Default = new Filter() {
        @Override
        public RpcResponse preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public RpcResponse postFilter(RpcRequest request, RpcResponse response) {
            return response;
        }

    };
}
