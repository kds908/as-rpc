package as.rpc.core.api;

import as.rpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @author abners.
 * @description 负载均衡
 * 权重、随机、轮询、自适应
 *
 * @date 2024/3/26 11:10
 */
public interface LoadBalancer<T> {
    T choose(List<T> providers);

    LoadBalancer<InstanceMeta> Default = p -> (p == null || p.size() == 0) ? null : p.get(0);
}
