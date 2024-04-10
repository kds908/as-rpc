package as.rpc.core.api;

import as.rpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Description for this class
 *
 * {@code {@author} abners.
 * {@code {@date}} 2024/3/26 14:22
 */
@Data
public class RpcContext {
    List<Filter> filters;

    Router<InstanceMeta> router;

    LoadBalancer<InstanceMeta> loadBalancer;
}
