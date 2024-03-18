package as.rpc.core.api;

import lombok.Data;

@Data
public class RpcRequest {
    // 调用服务 eg: as.rpc.demo.api.UserService
    private String service;
    // 调用方法 eg: as.rpc.demo.api.UserService#findById
    private String method;
    // 参数 eg : 100
    private Object[] args;
}
