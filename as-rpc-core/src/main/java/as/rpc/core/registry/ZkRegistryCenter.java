package as.rpc.core.registry;

import as.rpc.core.api.RegistryCenter;

import java.util.List;

/**
 * zookeeper registry center
 * <p>
 * {@code @author} abners.
 * <p>
 * {@code @date} 2024/3/26 15:22
 */
public class ZkRegistryCenter implements RegistryCenter {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(String service, String instance) {

    }

    @Override
    public void unregister(String service, String instance) {

    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }
}
