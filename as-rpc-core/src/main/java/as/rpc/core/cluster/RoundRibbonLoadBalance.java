package as.rpc.core.cluster;

import as.rpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author abners.
 * @description
 * @date 2024/3/26 11:58
 */
public class RoundRibbonLoadBalance<T> implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) return null;
        if (providers.size() == 1) return providers.get(0);
        // 保证index永为正数
        return providers.get((index.incrementAndGet() & 0x7fffffff) % providers.size());
    }
}
