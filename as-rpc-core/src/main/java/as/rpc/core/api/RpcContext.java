package as.rpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * Description for this class
 *
 * {@code {@author} abners.
 * {@code {@date}} 2024/3/26 14:22
 */
@Data
public class RpcContext {
    // todo
    List<Filter> filters;

    Router router;

    LoadBalancer loadBalancer;
}
