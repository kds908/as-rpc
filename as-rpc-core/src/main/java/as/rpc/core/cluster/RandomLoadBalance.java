package as.rpc.core.cluster;

import as.rpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @author abners.
 * @description
 * @date 2024/3/26 11:58
 */
public class RandomLoadBalance<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.size() == 0) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get(random.nextInt(providers.size()));
    }
}
